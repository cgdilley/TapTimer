package com.sprelf.taptimer.Models;

import android.content.Context;
import android.os.Parcelable;
import android.util.JsonWriter;
import android.view.View;

import com.sprelf.taptimer.Views.EmojiPickerView;

import java.io.IOException;

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
 * Created by Chris on 16.11.2016.
 */

public abstract class Prefab implements Parcelable
{
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_TIMERS = "timers";
    public static final String PREFAB_TIMER = "Prefab_Timer";

    /**
     * Creates a new Prefab object, holding all the same values.
     *
     * @return The copied Prefab object.
     */
    public abstract Prefab copy();

    /**
     * Saves the current contents of this Prefab object into SharedPreferences.
     *
     * @param c        Context within which to perform the operation
     * @param widgetId Widget ID to store the values under
     */
    public abstract void save(Context c, int widgetId);

    /**
     * Performs the work of manipulating the view based on the contents of the associated
     * Prefab.
     *
     * @param view View to manipulate.
     */
    public abstract void attachDataToView(View view);

    /**
     * Gets the resource identifier of the layout for rendering this Prefab object.
     *
     * @return Resource identifier for the rendering layout.
     */
    public abstract int getLayoutResource();

    /**
     * Gets the duration value of this prefab.  If no such value exists, returns 0.
     *
     * @return The duration of this prefab, or 0 if this prefab doesn't have a duration.
     */
    public abstract int getDuration();

    // TODO: DOCUMENT

    public abstract View getConfigLayout(Context c);

    public abstract void absorbConfigViewValues(View view);

    public abstract EmojiPickerView identifyEmojiPickerView(View view);


    public abstract void writeToJSON(JsonWriter writer) throws IOException;


    protected static String[] codePointArray(String in)
    {
        String[] returnArray = new String[in.length()];
        for (int i = 0; i < in.length(); i++)
            returnArray[i] = Integer.toHexString(Character.codePointAt(in, i));
        return returnArray;
    }

    protected static String sanitizeIconString(String icon)
    {
        /*
        for (int i = 0; i < Math.min(2, icon.length()); i++)
        {
            if (Integer.toHexString(Character.codePointAt(icon, i)).equals("de34"))
                return icon.substring(0, i + 1);
        }
        */
        return icon.substring(0, Math.min(2, icon.length()));
    }


}
