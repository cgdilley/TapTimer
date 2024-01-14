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

package com.sprelf.taptimer.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.sprelf.taptimer.Activities.ConfigActivity;
import com.sprelf.taptimer.Models.ActiveItem;
import com.sprelf.taptimer.Models.ActiveTimer;
import com.sprelf.taptimer.Models.Prefab_Timer;
import com.sprelf.taptimer.R;

public class WidgetHelpers
{
//    public static String WIDGET_PLACEMENT_INTENT = "com.sprelf.taptimer.WIDGET_PLACED";

    public static void placeTimerWidget(Context c)
    {
        AppWidgetManager awm = AppWidgetManager.getInstance(c);
        ComponentName provider = new ComponentName(c, TimerWidget.class);

        if (awm.isRequestPinAppWidgetSupported()) {
            Intent i = new Intent(c, TimerWidget.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setAction(WidgetBase.ACTION_CONFIGURE_LATEST);
            PendingIntent callback = PendingIntent.getBroadcast(
                    c, 0, i,
                    PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
            RemoteViews views = new RemoteViews(c.getPackageName(),
                                                R.layout.widget);
            Bundle b = new Bundle();
            b.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, views);

            awm.requestPinAppWidget(provider, b, callback);
        }
    }
}
