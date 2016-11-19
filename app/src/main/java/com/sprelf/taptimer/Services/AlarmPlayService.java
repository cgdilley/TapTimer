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

public class AlarmPlayService extends Service
{
    AudioTask task;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        task = new AudioTask();

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null)
        {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null)
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        if (alert == null)
        {
            Log.e("[AlarmService]", "Could not find default ringtone.");
            stopSelf();
        }
        else
        {
            Log.d("[AlarmService]", "Executing alarm...");
            task.execute(alert);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        task.stop();
        task.cancel(true);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class AudioTask extends AsyncTask<Uri, Void, Void>
    {
        private MediaPlayer mp;

        public void resume()
        {
            if (mp != null && !mp.isPlaying())
                mp.start();
        }

        public void pause()
        {
            if (mp != null && mp.isPlaying())
                mp.pause();
        }

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


        @Override
        protected Void doInBackground(Uri... params)
        {
            if (params.length != 1)
                return null;

            Uri res = params[0];

            try {
                mp = new MediaPlayer();
                mp.reset();
                mp.setDataSource(getApplicationContext(), res);
                mp.setLooping(true);
                mp.setAudioStreamType(AudioManager.STREAM_ALARM);
                mp.prepare();
                mp.start();

            } catch (Exception e) {
                Log.e("[AlarmService]", "Error encountered while starting alarm.\n" + e.getMessage());
            }


            return null;
        }
    }
}
