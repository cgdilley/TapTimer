/*
 *     TapTimer - A Timer Widget App
 *     Copyright (C) 2016 Dilley, Christopher
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sprelf.taptimer.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Utils.WakeLocker;
import com.sprelf.taptimer.Widgets.TimerWidget;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import static com.sprelf.taptimer.Widgets.WidgetBase.ACTION_ALARM_STOP;

/*
 * TapTimer - A Timer Widget App
 * Copyright (C) 2016 Dilley, Christopher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Service responsible for playing the alarm sound.
 */
public class AlarmPlayService extends Service
{
    // ID for notifications
    public static final int NOTIFICATION_ID = 101;
    public static final String CHANNEL_ID = "taptimer:ALARM";

    public static final String WAKELOCK_TAG = "taptimer:ALARM_WAKELOCK";

    // Custom ASyncTask for handling loading and managing of alarm's MediaPlayer object
    private AudioTask task;


    /**
     * @inheritDoc
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Context c = getApplicationContext();

        WakeLocker.acquire(c, WAKELOCK_TAG);

        // Initialize new audio task ASync object
        task = new AudioTask();

        Uri alert = _getAlert(c);
        // If somehow no ringtone was found, give up and stop
        if (alert == null)
        {
            Log.e("[AlarmService]", "Could not find default ringtone.");
            stopSelf();
        }
        // Otherwise, start the alarm
        else
        {
            Log.d("[AlarmService]", "Executing alarm...");
            // Start the alarm asynchronously
            task.execute(alert);

//            // Acquire a WakeLock to keep the screen on while this service is running
//            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//
//            wakeLock = pm.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                                      PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKELOCK_TAG);
//            if (wakeLock != null)
//                wakeLock.acquire(20000);

            // Open the notification for dismissing the alarm
            openNotification(this);
        }

        return START_STICKY;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDestroy()
    {
        Log.d("[AlarmPlayService]", "Destroying alarm service.");

        // When this service is destroyed, stop the alarm
        task.stop();
        task.cancel(true);

        // Release the wake lock
        WakeLocker.release();

        // Close the persistent notification
        closeNotification(this);

        super.onDestroy();
    }


    /**
     * @inheritDoc
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Opens a persistent notification that provides a button to allow a user to stop the alarm.
     *
     * @param c Context within which to perform the operation.
     */
    private static void openNotification(Context c)
    {
        NotificationManager manager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                c.getString(R.string.AlarmNotification_ChannelName),
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(c.getString(R.string.AlarmNotification_ChannelDescription));
        manager.createNotificationChannel(channel);

        // Create pending intent to include with the notification that will send a broadcast to
        // disable the alarm.
        Intent notiIntent = new Intent(c, TimerWidget.class);
        notiIntent.setAction(ACTION_ALARM_STOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, 0, notiIntent,
                                                                 PendingIntent.FLAG_IMMUTABLE);

        Log.d("[AlarmPlayService]", "Attempting to produce notification.");
        // Construct the notification, attaching the above pending intent
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(c, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon)
                        .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.icon))
                        .setContentTitle(c.getString(R.string.AlarmNotification_Title))
                        .setContentText(c.getString(R.string.AlarmNotification_Message))
                        .setContentIntent(pendingIntent)
                        .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                                   c.getString(R.string.AlarmNotification_Button),
                                   pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setAutoCancel(true);

        manager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Closes the persistent alarm dismissal notification.
     *
     * @param c Context within which to perform the operation.
     */
    private static void closeNotification(Context c)
    {
        NotificationManager manager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(NOTIFICATION_ID);
    }

    private Uri _getAlert(Context c)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        String alertPref = prefs.getString("Pref_Ringtone", null);
        if (alertPref != null)
            return Uri.parse(alertPref);

        // Get the URI to the default alarm ringtone
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        // If the ringtone was not found, find backup
        if (alert == null)
        {
            // Get the URI to the default notification ringtone
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // If the ringtone was not found, find backup
            if (alert == null)
                // Get the URI to the default ringing ringtone
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        return alert;
    }


    /**
     * Custom AsyncTask object for managing the alarm's media player.
     */
    private class AudioTask extends AsyncTask<Uri, Void, Void>
    {
        // MediaPlayer object to play alarm through
        private MediaPlayer mp;

        /**
         * Resume the MediaPlayer, if paused.
         */
        public void resume()
        {
            if (mp != null && !mp.isPlaying())
                mp.start();
        }

        /**
         * Pause the MediaPlayer, if playing.
         */
        public void pause()
        {
            if (mp != null && mp.isPlaying())
                mp.pause();
        }

        /**
         * Stop the MediaPlayer, and release it from memory.
         */
        public void stop()
        {
            if (mp != null)
            {
                if (mp.isPlaying())
                    mp.stop();
                mp.reset();
                mp.release();
                mp = null;
            }
        }


        /**
         * @inheritDoc For AudioTask, sets up the MediaPlayer and starts it playing.
         */
        @Override
        protected Void doInBackground(Uri... params)
        {
            // If no parameters were given, abort
            if (params.length != 1)
                return null;

            // Extract the URI parameter
            Uri res = params[0];

            try
            {
                // Construct the media player
                mp = new MediaPlayer();
                mp.reset();
                // Set the given URI as the data source
                mp.setDataSource(getApplicationContext(), res);
                // Set the sound to loop
                mp.setLooping(true);
                // Force the audio to play through alarm audio stream
                mp.setAudioStreamType(AudioManager.STREAM_ALARM);
                // Start the alarm
                mp.prepare();
                mp.start();

            } catch (Exception e)
            {
                Log.e("[AlarmService]", "Error encountered while starting alarm.\n" + e.getMessage());
            }

            return null;
        }
    }
}
