package com.sprelf.taptimer.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Views.TimerWidgetView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * Class for handling all functionality related specifically to the Timer Widget.
 */
public class TimerWidget extends WidgetBase
{
    // Action identifier for updating this widget type
    public static final String ACTION_AUTO_UPDATE = "com.sprelf.taptimer.ACTION_UPDATE_TIMER";
    // Channel for update timer pending intents
    public static final int REFRESH_ALARM_ID = 0;
    // Ratio of how often a timer widget should refresh in relation to its total duration
    public static final float REFRESH_RATE_RATIO = 0.01f;

    // Keys for storing settings relating to this widget type
    public static final String KEY_PREFIX = "TimerWidget_";
    public static final String TIMER_START = KEY_PREFIX + "TimerStart";
    public static final String TIMER_PAUSE = KEY_PREFIX + "TimerPause";
    public static final String TIMER_DURATION = KEY_PREFIX + "TimerDuration";
    public static final String TIMER_NAME = KEY_PREFIX + "Name";
    public static final String TIMER_ICON = KEY_PREFIX + "Icon";
    public static final String TIMER_COLOR = KEY_PREFIX + "Color";
    public static final String ID_LIST = KEY_PREFIX + "IDList";

    /**
     * @inheritDoc
     */
    @Override
    protected void updateAppWidget(Context c, AppWidgetManager appWidgetManager,
                                   int widgetId, int width, int height)
    {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget);

        draw(c, widgetId, views, width, height, new TimerWidgetView(c));

        // Build the action to perform in response to the widget being clicked
        // In this case, performs an action broadcast that will be intercepted by this widget.
        Intent intent = new Intent(c, TimerWidget.class);
        intent.setAction(ACTION_TIMER_CLICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, widgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.WidgetView, pendingIntent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        long startTime = prefs.getLong(TIMER_START + widgetId, -1);
        long pauseTime = prefs.getLong(TIMER_PAUSE + widgetId, -1);
        long duration = prefs.getLong(TIMER_DURATION + widgetId, -1);
        if (pauseTime == -1 && startTime != -1 && duration != -1)
            setAlarm(c, widgetId, startTime + duration);
        else
            unsetAlarm(c, widgetId);

        // Instruct the widget manager to update the widget.
        // This is the final call in the update chain that forces the UI to update
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update(Context c)
    {
        AppWidgetManager manager = AppWidgetManager.getInstance(c);

        onUpdate(c, manager, manager.getAppWidgetIds(
                new ComponentName(c, TimerWidget.class)));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getActionIntentName()
    {
        return ACTION_AUTO_UPDATE;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getAlarmId()
    {
        return REFRESH_ALARM_ID;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void pruneWidgetSettings(Context c, AppWidgetManager manager)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor edit = prefs.edit();

        // Initialize sets for all valid settings keys as well as a set of existing IDs
        Set<String> validKeys = new HashSet<>();
        Set<String> currIds = new HashSet<>();

        // Iterate through all existing widget IDs, and generate all valid keys associated
        // with these widget IDs.
        for (int widgetId : manager.getAppWidgetIds(new ComponentName(c, TimerWidget.class)))
        {
            String id = Integer.toString(widgetId);
            currIds.add(id);

            validKeys.add(TIMER_START + id);
            validKeys.add(TIMER_DURATION + id);
            validKeys.add(TIMER_PAUSE + id);
            validKeys.add(TIMER_ICON + id);
            validKeys.add(TIMER_COLOR + id);
            validKeys.add(TIMER_NAME + id);
        }
        validKeys.add(ID_LIST);

        // Get the set containing all of the existing settings keys
        Set<String> allKeys = prefs.getAll().keySet();

        // Remove all valid keys from this set, leaving only the invalid keys
        allKeys.removeAll(validKeys);

        // Iterate through all these invalid keys.  If they share this widget's key prefix,
        // then the setting is truly invalid and should be deleted.
        for (String invalidKey : allKeys)
        {
            if (invalidKey.startsWith(KEY_PREFIX))
                edit.remove(invalidKey);
        }

        // Update the list of active IDs (this may be outdated and no longer necessary)
        edit.putStringSet(ID_LIST, currIds);

        edit.apply();
    }

    /**
     * @inheritDoc For TimerWidgets, clicking pauses and unpauses the timer.
     */
    @Override
    protected void respondToClick(Context c, Intent intent)
    {
        // Stop the alarm sound, if it is playing
        stopAlarm(c);

        // Get the widget ID from the given intent.  If it doesn't exist, cancel this operation.
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (widgetId == -1)
            return;

        Log.d("[Widget]", "RESPONDING TO CLICK (Widget id# " + widgetId + ")...");

        String id = Integer.toString(widgetId);

        // Get objects for reading and writing shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor edit = prefs.edit();

        // Get the start time and duration of this timer widget
        long startTime = prefs.getLong(TIMER_START + id, -1);
        long duration = prefs.getLong(TIMER_DURATION + id, -1);
        // If settings do not yet exist for this widget ID, cancel this operation.
        if (startTime == -1 || duration == -1)
            return;

        // Get the current time
        long currTime = System.currentTimeMillis();
        // Get the pause time for this widget.  If it doesn't exist, this will be set to -1
        long pauseTime = prefs.getLong(TIMER_PAUSE + id, -1);
        // If the timer is not paused, pause it by adding a pause time
        if (pauseTime == -1)
        {
            edit.putLong(TIMER_PAUSE + id, currTime);
            if (currTime - startTime >= duration)
                edit.putLong(TIMER_START + id, currTime);
        }
        // If the timer is paused, unpause it by recalculating the startTime and removing the
        // pause time.
        else
        {
            // Calculate the amount of time that has passed since the timer was paused
            long timeSincePause = currTime - pauseTime;
            // Shift the start time by that amount, so the timer behaves as if the correct
            // amount of time has passed.
            long newStartTime = startTime + timeSincePause;

            // Update the start time and remove the pause time
            edit.putLong(TIMER_START + id, newStartTime);
            edit.remove(TIMER_PAUSE + id);
        }

        // Forcing synchronous commit to ensure values are changed before rendering update
        edit.commit();

        Log.d("[Widget]", "Preferences:  " + prefs.getAll().toString());

        // Force an update and restart timer for future updates
        update(c);
        startUpdateTimer(c);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected float calculatePercentage(Context c, int widgetId)
    {
        String id = Integer.toString(widgetId);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        // Test that the relevant values are found
        if (!prefs.contains(TIMER_START + id) || !prefs.contains(TIMER_DURATION + id))
        {
            Log.d("[Widget]", "Could not calculate percentage (missing values).");
            return 1f;
        }

        // Extract the relevant values
        long startTime = prefs.getLong(TIMER_START + id, -1);
        long duration = prefs.getLong(TIMER_DURATION + id, -1);
        long pauseTime = prefs.getLong(TIMER_PAUSE + id, -1);

        // Calculate percentage of remaining time
        return calculatePercentage(startTime, pauseTime, duration);

    }

    /**
     * Calculates the percentage of remaining time of a TimerWidget based on the given values.
     *
     * @param startTime Time that the timer was started on.
     * @param pauseTime Time that the timer was paused on.  If the timer is not paused, this value
     *                  should equal -1.
     * @param duration  Duration of the timer, in milliseconds.
     * @return A value between 0 and 1 representing the percentage of remaining time on the timer.
     */
    public static float calculatePercentage(long startTime, long pauseTime, long duration)
    {
        // Calculate the amount of elapsed time on the timer
        long timeElapsed = pauseTime == -1 ? System.currentTimeMillis() - startTime
                                           : pauseTime - startTime;

        // If no time has elapsed, return 100% time remaining
        // If the full duration has elapsed, return 0% time remaining
        // Otherwise, calculate the percentage of remaining time.
        if (timeElapsed <= 0 || duration <= 0)
            return 1f;
        else if (timeElapsed > duration)
            return 0f;
        else
            return 1f - (timeElapsed / (float) duration);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected int getRefreshRate(Context c)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        // Get the default idle refresh rates and the minimum refresh rate
        int idleRate = c.getResources().getInteger(R.integer.IdleRefreshRate);
        int minimumRate = c.getResources().getInteger(R.integer.MinimumRefreshRate);

        // Start a list of refresh rates for all active widgets to compare
        List<Integer> refreshRates = new ArrayList<>();
        // Add the idle rate as a baseline to fall back on if no widgets exist
        refreshRates.add(idleRate);

        // Iterate through the IDs of all active timer widgets
        for (int widgetId : AppWidgetManager.getInstance(c).getAppWidgetIds(
                new ComponentName(c, TimerWidget.class)))
        {
            String id = Integer.toString(widgetId);
            // If the timer widget is active (not paused)
            if (!prefs.contains(TimerWidget.TIMER_PAUSE + id))
            {
                // Get the duration of the timer, and add its optimal refresh rate based on its
                // duration to the list of refresh rates.
                long duration = prefs.getLong(TimerWidget.TIMER_DURATION + id, 0);
                if (duration > 0)
                    refreshRates.add((int) Math.max(duration * REFRESH_RATE_RATIO, minimumRate));
            }
        }

        // Return the smallest refresh rate in the list of refresh rates
        return Collections.min(refreshRates);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void fireAlarm(Context c)
    {
        startAlarm(c);
    }
}
