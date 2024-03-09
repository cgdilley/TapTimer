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

package com.sprelf.taptimer.Utils;

import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;


public abstract class WakeLocker
{
    private static PowerManager.WakeLock _lock;

    public static void acquire(Context c, String tag)
    {
        if (_lock != null) _lock.release();

        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        _lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                               PowerManager.ACQUIRE_CAUSES_WAKEUP |
                               PowerManager.ON_AFTER_RELEASE,
                               tag);
        _lock.acquire(20000);
    }

    public static void release()
    {
        if (_lock != null) {
            _lock.release();
            _lock = null;
        }
    }
}
