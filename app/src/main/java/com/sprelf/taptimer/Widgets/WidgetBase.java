package com.sprelf.taptimer.Widgets;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.sprelf.taptimer.Services.AlarmPlayService;
import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Views.BaseWidgetView;

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
 * Created by Chris on 08.01.2016.
 * THIS WAS CREATED FOR THE FLIGHT BATTERY APP, MODIFIED TO SUIT THE NEEDS OF THIS APP
 */
public abstract class WidgetBase extends AppWidgetProvider
{
    // Action descriptor for click events
    public static final String ACTION_TIMER_CLICK = "com.sprelf.taptimer.ACTION_TIMER_CLICK";
    public static final String ACTION_ALARM_FIRE = "com.sprelf.taptimer.ACTION_ALARM_FIRE";
    public static final String ACTION_ALARM_STOP = "com.sprelf.taptimer.ACTION_ALARM_STOP";

    public static final int ALARM_REQUEST_CODE = 40000;
    public static final int NOTIFICATION_ID = 101;

    /**
     * Creates the {@link RemoteViews} object, renders the widget onto it, and triggers a
     * widget update.
     *
     * @param c                Context within which to perform the operation.
     * @param appWidgetManager The app widget manager.
     * @param widgetId         The widget's unique identifier.
     * @param width            The width of the widget's drawing area (in pixels).
     * @param height           The height of the widget's drawing area (in pixels).
     */
    abstract protected void updateAppWidget(Context c, AppWidgetManager appWidgetManager,
                                            int widgetId, int width, int height);

    /**
     * Forces an update of all widgets of the subclass's type.
     *
     * @param c Context within which to perform the operation.
     */
    abstract public void update(Context c);

    /**
     * Returns the subclass's unique action intent name for intercepting update broadcasts.
     *
     * @return This widget's action intent name.
     */
    abstract public String getActionIntentName();

    /**
     * Returns the subclass's unique alarm ID.
     *
     * @return This widget's alarm ID.
     */
    abstract public int getAlarmId();

    /**
     * Acquires a list of all running widgets of the same time, and deletes all settings
     * relating to widgets of the subclass's type that belong to widget IDs that no longer exist.
     *
     * @param c       Context within which to perform the operation
     * @param manager The app widget manager
     */
    abstract protected void pruneWidgetSettings(Context c, AppWidgetManager manager);

    /**
     * Performs all actions in response to a particular widget being clicked on.  The given intent
     * carries all information stored in the PendingIntent which performed the broadcast.
     *
     * @param c      Context within which to perform the operation
     * @param intent The intent sent with the click-resposne broadcast.  Carries, at minimum,
     *               the widget ID of the widget that was clicked under the key
     *               AppWidgetManager.EXTRA_APPWIDGET_ID
     */
    abstract protected void respondToClick(Context c, Intent intent);

    /**
     * Performs the calculations for the percentage of remaining time on a timer for the given
     * widget ID.
     *
     * @param c        Context within which to perform the operation.
     * @param widgetId Widget ID of the widget to calculate the percentage of remaining time for.
     * @return A value between 0 and 1 representing the percentage of time REMAINING in the timer.
     */
    abstract protected float calculatePercentage(Context c, int widgetId);

    /**
     * Calculate the optimal refresh rate for the collection of widgets of the subclass's type.
     *
     * @param c Context within which to perform the operation.
     * @return The optimal refresh rate, in milliseconds.
     */
    abstract protected int getRefreshRate(Context c);

    abstract protected void fireAlarm(Context c);

    /**
     * Iterates through all given widget IDs and updates each member.  This is called both by
     * the Android system's periodic updater as well as the app's forced updates.
     *
     * @param context      Context within which to perform the operation.
     * @param manager      The app widget manager.
     * @param appWidgetIds A list of widget IDs to update.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds)
    {        // Take the time to clear all unused settings
        pruneWidgetSettings(context, manager);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds)
        {
            Bundle options = manager.getAppWidgetOptions(appWidgetId);
            updateAppWidget(context, manager, appWidgetId,
                            options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH),
                            options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT));

        }
    }

    /**
     * @inheritDoc For TapTimer widgets, starts the forced-update timer and forces an initial update.
     */
    @Override
    public void onEnabled(Context context)
    {
        Log.d("[Widget]", "ENABLED.");

        startUpdateTimer(context);
        update(context);
    }

    /**
     * @inheritDoc For TapTimer widgets, stops the forced-update timer and removes all widget settings.
     */
    @Override
    public void onDisabled(Context context)
    {
        stopUpdateTimer(context);
        pruneWidgetSettings(context, AppWidgetManager.getInstance(context));
    }


    /**
     * @inheritDoc For TapTimer Widgets, forces a widget update with the new width and height values.
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager manager,
                                          int appWidgetId, Bundle newOptions)
    {
        updateAppWidget(context, manager, appWidgetId,
                        newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH),
                        newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT));
    }

    /**
     * @inheritDoc For TapTimer Widgets, intercepts broadcasts for forced updates, click events,
     * and alarm events.  For forced updates, forces an update if the intent action matches the
     * subclass's action intent name.  For click events, passes the click event along to the
     * subclass to handle.  For alarm events, allows the subclass to trigger its alarm.
     */
    @Override
    public void onReceive(Context c, Intent intent)
    {
        super.onReceive(c, intent);

        if (intent.getAction().equals(getActionIntentName()))
        {
            Log.d("[Widget]", "FORCING UPDATE...");
            update(c);
            startUpdateTimer(c);
        }
        else if (intent.getAction().equals(ACTION_TIMER_CLICK))
        {
            Log.d("[Widget]", "CLICK RECEIVED.");
            respondToClick(c, intent);
        }
        else if (intent.getAction().equals(ACTION_ALARM_FIRE))
        {
            Log.d("[Widget]", "FIRING ALARM.");
            fireAlarm(c);
        }
        else if (intent.getAction().equals(ACTION_ALARM_STOP))
        {
            Log.d("[Widget]", "STOPPING ALARM.");
            stopAlarm(c);
        }

    }

    /**
     * Renders the given widget view onto the given RemoteViews.
     *
     * @param c          Context within which to perform the operation.
     * @param widgetId   The widget's unique identifier
     * @param views      The RemoteViews to draw on, to be rendered by the widget.
     * @param width      Width of the widget's drawing area (in di-pixels).
     * @param height     The height of the widget's drawing area (in di-pixels).
     * @param renderView Custom Widget view to render.
     */
    protected void draw(Context c, int widgetId, RemoteViews views, int width, int height,
                        BaseWidgetView renderView)
    {
        // If we have invalid dimensions, abort rendering
        if (!(width > 0 && height > 0))
            return;

        // Get the pixel density of the screen.  Widgets are internally rendered at the given width
        // and height dimensions scaled by this value, and then scaled back by this value for
        // actually rendering to screen.  This is to allow the widget to take advantage of real
        // pixels rather than simply density-independent pixels, while still fitting the prescribed
        // density-independent pixel dimensions.
        float prerenderScale = c.getResources().getDisplayMetrics().density;

        // Set the parameters of the widget's rendering view, and lay it out.
        renderView.setPercentage(calculatePercentage(c, widgetId));
        renderView.setWidgetId(widgetId);
        renderView.measure((int) (width * prerenderScale), (int) (height * prerenderScale));
        renderView.layout(0, 0, (int) (width * prerenderScale), (int) (height * prerenderScale));

        // Generate an empty bitmap to draw onto.  This is necessary as Android does not allow
        // custom Views in widgets.  However, as ImageViews are allowed, we get around this by
        // rendering a custom View to a bitmap, and then applying that bitmap to the ImageView.
        Bitmap bitmap = Bitmap.createBitmap((int) (width * prerenderScale),
                                            (int) (height * prerenderScale),
                                            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the view onto this bitmap
        renderView.draw(canvas);
        // Scale the bitmap back down
        canvas.scale(1f / prerenderScale, 1f / prerenderScale);

        // Set the image view in the widget to this generated bitmap
        views.setImageViewBitmap(R.id.WidgetView, bitmap);
    }

    /**
     * Starts the timer service for updating the widgets.  This fires a one-time timer after a
     * certain refresh time has elapsed, which forces a widget update, and sets the next timer
     * with an updated refresh time.
     *
     * @param c Context within which to perform the operation.
     */
    protected void startUpdateTimer(Context c)
    {
        // Get the current time
        long currTime = System.currentTimeMillis();

        // Calculate the optimal refresh rate based on the active widgets
        int refreshRate = getRefreshRate(c);

        Log.d("[Widget]", "Refresh rate: " + refreshRate);

        // Create the update broadcast pending intent
        Intent alarmIntent = new Intent(getActionIntentName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, getAlarmId(), alarmIntent,
                                                                 PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        // If the refresh rate is low enough, we want the updates to occur more precisely, and so
        // we use AlarmManager.setExact(...).  However, this method is only available on SDKs >= 19.
        // If we can't, or don't need to, set an exact timer, sets an inexact one instead.
        // AlarmManager.RTC ensures that this timer does not wake a sleeping device, as there is no
        // need to push an update in this case (as opposed to AlarmManager.RTC_WAKEUP).
        if (refreshRate < 30000 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC, currTime + refreshRate,
                                  pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC, currTime + refreshRate,
                             pendingIntent);
    }

    /**
     * Stops the update timer for updating the widgets.
     *
     * @param c Context within which to perform the operation.
     */
    protected void stopUpdateTimer(Context c)
    {
        // Create the update broadcast pending intent to identify
        Intent alarmIntent = new Intent(getActionIntentName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, getAlarmId(), alarmIntent,
                                                                 PendingIntent.FLAG_CANCEL_CURRENT);

        // Remove all matching existing pending intents
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    protected void setAlarm(Context c, int widgetId, long time)
    {
        if (time < System.currentTimeMillis())
            return;

        Intent intent = new Intent(ACTION_ALARM_FIRE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(c, ALARM_REQUEST_CODE + widgetId, intent,
                                           PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    protected void unsetAlarm(Context c, int widgetId)
    {
        Intent intent = new Intent(ACTION_ALARM_FIRE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(c, ALARM_REQUEST_CODE + widgetId, intent,
                                           PendingIntent.FLAG_CANCEL_CURRENT);

        // Remove all matching existing pending intents
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    protected void startAlarm(Context c)
    {
        Log.d("[Widget]", "Starting alarm...");
        Intent intent = new Intent(c, AlarmPlayService.class);
        c.startService(intent);

        Intent notiIntent = new Intent(ACTION_ALARM_STOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, 0, notiIntent,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.icon))
                .setContentTitle(c.getString(R.string.AlarmNotification_Title))
                .setContentText(c.getString(R.string.AlarmNotification_Message))
                .setContentIntent(pendingIntent)
                .setPriority(2)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, builder.build());


    }

    protected void stopAlarm(Context c)
    {
        Intent intent = new Intent(c, AlarmPlayService.class);
        c.stopService(intent);
    }


}
