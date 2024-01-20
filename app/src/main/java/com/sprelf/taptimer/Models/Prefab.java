package com.sprelf.taptimer.Models;

import android.content.Context;
import android.util.JsonWriter;
import android.view.View;

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
 * Class for holding settings about a widget that may be shared among multiple independent widgets.
 */
public abstract class Prefab implements Configurable
{
    // JSON field name for identifying Prefab type
    public static final String FIELD_TYPE = "type";
    // JSON field name for identifying timers array
    public static final String FIELD_TIMERS = "timers";
    // JSON value (for type field) for describing Prefab_Timer objects
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
     * Gets the duration value of this prefab.  If no such value exists, returns 0.
     *
     * @return The duration of this prefab, or 0 if this prefab doesn't have a duration.
     */
    public abstract int getDuration();


    /** Writes the contents of this Prefab into the given JSON writer.
     *
     * @param writer JSON writer to write Prefab data into.
     * @throws IOException Throws IOException if any issues occur when writing.
     */
    public abstract void writeToJSON(JsonWriter writer) throws IOException;


    /** @inheritDoc
     */
    public abstract void attachDataToConfigPreview(View view);

    /** @inheritDoc
     */
    public abstract int getConfigPreviewResource();

    /** @inheritDoc
     */
    public abstract View getConfigView(Context c);

    /** @inheritDoc
     */
    public abstract void absorbConfigViewValues(View view);



    /** Converts the given array of hexadecimal unicode codepoints into its equivalent String.
     *
     * @param codepoints Array of hexadecimal unicode codepoints to convert.
     * @return The string representation of the given codepoints.
     */
    protected static String parseCodepoints(String[] codepoints)
    {
        StringBuilder iconString = new StringBuilder();
        for (String codepoint : codepoints)
        {
            try {
                int hexVal = Integer.parseInt(codepoint, 16);
                iconString.append(new String(Character.toChars(hexVal)));
            } catch (NumberFormatException e) {

                return codepoints[0];
            }
        }
        return iconString.toString();
    }

    /** Converts the given string into its equivalent array of hexadecimal unicode codepoints.
     *
     * @param in String to convert.
     * @return The hexadecimal codepoints string representation of the given string.
     */
    protected static String[] codePointArray(String in)
    {
//        String[] returnArray = new String[in.length()];
//        for (int i = 0; i < in.length(); i++)
//            returnArray[i] = Integer.toHexString(Character.codePointAt(in, i));
//        return returnArray;
        return new String[]{in};
    }


}
