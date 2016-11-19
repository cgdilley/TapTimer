package com.sprelf.taptimer.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Views.EmojiPickerView;
import com.sprelf.taptimer.Views.TimerWidgetView;
import com.sprelf.taptimer.Widgets.TimerWidget;

import java.util.HashSet;
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
 * Created by Chris on 15.11.2016.
 * Class for holding all information relating to an active timer
 */

public class ActiveTimer extends ActiveItem
{
    // Starting time and pausing time of the timer
    private long startTime, pauseTime;
    // Whether or not the timer is currently paused
    private boolean isPaused;

    /**
     * Private constructor, use static constructors instead.
     */
    private ActiveTimer(Prefab prefab, long startTime, long pauseTime, boolean isPaused, int widgetId)
    {
        this.prefab = prefab;
        this.startTime = startTime;
        this.pauseTime = pauseTime;
        this.isPaused = isPaused;
        this.widgetId = widgetId;
    }

    /**
     * Builds an active timer from stored SharedPreferences associated with the given widget ID.
     *
     * @param c        Context within which to perform the operation.
     * @param widgetId ID of the widget to represent.
     * @param prefab   Prefab to attach to this active item
     * @return The newly constructed ActiveTimer object.  If construction failed, returns null.
     */
    public static ActiveTimer build(Context c, int widgetId, Prefab prefab)
    {
        // If the given prefab is null, return null
        if (prefab == null)
            return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        // If the timer start prefab cannot be found, return null
        if (!prefs.contains(TimerWidget.TIMER_START + widgetId))
            return null;

        // Extract the start and pause time of this timer.  If there is no pause time,
        // mark the timer as unpaused.
        long startTime = prefs.getLong(TimerWidget.TIMER_START + widgetId, 0);
        long pauseTime = prefs.getLong(TimerWidget.TIMER_PAUSE + widgetId, -1);
        boolean isPaused = (pauseTime != -1);

        // Return the newly constructed active timer
        return new ActiveTimer(prefab, startTime, pauseTime, isPaused, widgetId);
    }

    /**
     * Builds a new active timer from scratch, using the given prefab.  This timer starts
     * out paused, and uses the current time as both the start and pause times.
     *
     * @param widgetId Widget ID to assign to this active timer
     * @param prefab   Prefab timer prefab to attach to this timer
     * @return The newly constructed ActiveTimer object.
     */
    public static ActiveTimer buildNew(int widgetId, Prefab prefab)
    {
        if (prefab == null)
            return null;

        long currTime = System.currentTimeMillis();

        return new ActiveTimer(prefab, currTime, currTime, true, widgetId);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void save(Context c)
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor edit = prefs.edit();

        String id = Integer.toString(widgetId);

        // Add the timing info
        edit.putLong(TimerWidget.TIMER_START + id, startTime);
        if (isPaused)
            edit.putLong(TimerWidget.TIMER_PAUSE + id, pauseTime);

        // Update the id list of active timers (may not be necessary?)
        Set<String> ids = prefs.contains(TimerWidget.ID_LIST)
                          ? prefs.getStringSet(TimerWidget.ID_LIST, null)
                          : new HashSet<String>();
        if (ids != null)
        {
            ids.add(id);
            edit.putStringSet(TimerWidget.ID_LIST, ids);
        }

        // Save the changed preferences synchronously
        edit.commit();


        // Add the prefab info
        prefab.save(c, widgetId);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getConfigLayoutResource()
    {
        return R.layout.layout_activeitem_timer;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void attachDataToView(View view)
    {
        TimerWidgetView twv = (TimerWidgetView) view.findViewById(R.id.ActiveItem_Widget);
        if (twv == null)
        {
            Log.d("[ActiveTimer]", "View not properly inflated.");
            return;
        }

        float percentage = TimerWidget.calculatePercentage(startTime,
                                                           isPaused ? pauseTime
                                                                    : -1,
                                                           prefab.getDuration() * 1000);
        twv.setPercentage(percentage);
        twv.setWidgetId(widgetId);
        twv.setFadeIfInactive(false);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void reset()
    {
        long currTime = System.currentTimeMillis();
        startTime = currTime;
        pauseTime = currTime;
        isPaused = true;
    }

    @Override
    public View getConfigLayout(Context c)
    {
        // TODO:  May want to change this to inflate into parent view, by passing parent arg
        View view = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.dialog_config_activetimer, null);

        view.findViewById(R.id.ActiveTimer_Config_ResetButton)
            .setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    reset();
                }
            });

        ViewGroup prefabArea = (ViewGroup) view.findViewById(R.id.ActiveTimer_Config_CustomPrefabArea);
        prefabArea.addView(prefab.getConfigLayout(c));

        return view;
    }

    @Override
    public EmojiPickerView identifyEmojiPickerView(View view)
    {
        return prefab.identifyEmojiPickerView(view);
    }

    @Override
    public void absorbConfigViewValues(View view)
    {
        prefab.absorbConfigViewValues(view.findViewById(R.id.ActiveTimer_Config_CustomPrefabArea));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString()
    {
        return "ActiveTimer{" +
               "prefab=" + prefab +
               ", startTime=" + startTime +
               ", pauseTime=" + pauseTime +
               ", isPaused=" + isPaused +
               ", widgetId=" + widgetId +
               '}';
    }


    /// PARCELLING STUFF


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(startTime);
        dest.writeLong(isPaused ? pauseTime : -1);
        dest.writeInt(widgetId);
        dest.writeParcelable(prefab, flags);
    }


    public static final Parcelable.Creator<ActiveTimer> CREATOR =
            new Parcelable.Creator<ActiveTimer>()
            {
                public ActiveTimer createFromParcel(Parcel in)
                {
                    return new ActiveTimer(in);
                }

                public ActiveTimer[] newArray(int size)
                {
                    return new ActiveTimer[size];
                }
            };

    private ActiveTimer(Parcel in)
    {
        startTime = in.readLong();
        pauseTime = in.readLong();
        isPaused = pauseTime != -1;
        widgetId = in.readInt();
        prefab = in.readParcelable(Prefab.class.getClassLoader());
    }
}

