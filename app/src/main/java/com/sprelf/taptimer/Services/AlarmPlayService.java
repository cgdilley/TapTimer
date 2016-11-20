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

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

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

/** Service responsible for playing the alarm sound.
 *
 */
public class AlarmPlayService extends Service
{
    // Custom ASyncTask for handling loading and managing of alarm's MediaPlayer object
    AudioTask task;

    /** @inheritDoc
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Initialize new audio task ASync object
        task = new AudioTask();

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
            task.execute(alert);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /** @inheritDoc
     */
    @Override
    public void onDestroy()
    {
        // When this service is destroyed, stop the alarm
        task.stop();
        task.cancel(true);

        super.onDestroy();
    }


    /** @inheritDoc
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /** Custom AsyncTask object for managing the alarm's media player.
     *
     */
    private class AudioTask extends AsyncTask<Uri, Void, Void>
    {
        // MediaPlayer object to play alarm through
        private MediaPlayer mp;

        /** Resume the MediaPlayer, if paused.
         *
         */
        public void resume()
        {
            if (mp != null && !mp.isPlaying())
                mp.start();
        }

        /** Pause the MediaPlayer, if playing.
         *
         */
        public void pause()
        {
            if (mp != null && mp.isPlaying())
                mp.pause();
        }

        /** Stop the MediaPlayer, and release it from memory.
         *
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


        /** @inheritDoc For AudioTask, sets up the MediaPlayer and starts it playing.
         */
        @Override
        protected Void doInBackground(Uri... params)
        {
            // If no parameters were given, abort
            if (params.length != 1)
                return null;

            // Extract the URI parameter
            Uri res = params[0];

            try {
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

            } catch (Exception e) {
                Log.e("[AlarmService]", "Error encountered while starting alarm.\n" + e.getMessage());
            }

            return null;
        }
    }
}
