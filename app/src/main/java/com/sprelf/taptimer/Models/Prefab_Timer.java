package com.sprelf.taptimer.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sprelf.taptimer.Emojicon.EmojiconHandler_Custom;
import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Utils.ColorUtils;
import com.sprelf.taptimer.Views.ColorSwitcherView;
import com.sprelf.taptimer.Views.DurationSetterView;
import com.sprelf.taptimer.Views.EmojiPickerView;
import com.sprelf.taptimer.Widgets.TimerWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

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
 * Class for holding all information relating to a timer prefab
 */

public class Prefab_Timer extends Prefab
{
    // Labels for the JSON file
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ICON = "icon";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_COLOR = "color";

    // Name of this timer prefab
    private String name;
    // Integer pointing to the icon resource for this prefab
    private String icon;
    // Duration of this timer prefab, in seconds
    private int duration;
    // Rendering color for this timer prefab
    private int color;

    /**
     * Private constructor, use static constructors instead.
     */
    private Prefab_Timer(String name, String icon, int duration, int color)
    {
        this.name = name;
        this.icon = sanitizeIconString(icon);
        this.duration = duration;
        this.color = color;
    }

    /**
     * Extracts the information from the given JSON object to build a Prefab_Timer object.
     *
     * @param info JSON object containing all relevant information.
     * @return The newly constructed Prefab_Timer object.
     * @throws JSONException Throws an exception if there was an error in processing the JSON object.
     */
    public static Prefab_Timer build(JSONObject info) throws JSONException
    {
        // Extract the JSON fields
        String name = info.getString(FIELD_NAME);
        JSONArray iconArray = info.getJSONArray(FIELD_ICON);
        int duration = info.getInt(FIELD_DURATION);
        String colorString = info.getString(FIELD_COLOR);

        // Convert the JSON array of codepoints strings for the icon into a string array
        String[] arr = new String[iconArray.length()];
        for (int i = 0; i < iconArray.length(); i++)
            arr[i] = iconArray.getString(i);

        // Get the string representation of the given codepoints
        String icon = parseCodepoints(arr);

        // Parses the string representing the color into an integer representation
        int color = Color.parseColor(colorString);

        // Construct the new Prefab_Timer and return it.
        return new Prefab_Timer(name, icon, duration, color);
    }

    /**
     * Constructs timer prefab from information already stored in SharedPreferences under
     * the given widget ID.
     *
     * @param c        Context within which to perform the operation.
     * @param widgetId ID of the widget whose values should be extracted
     * @return The newly constructed Prefab_Timer object.  If the process failed, returns null.
     */
    public static Prefab_Timer build(Context c, int widgetId)
    {
        // Check for the existence of all fields, and return null if any are missing.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        if (!prefs.contains(TimerWidget.TIMER_NAME + widgetId) ||
            !prefs.contains(TimerWidget.TIMER_ICON + widgetId) ||
            !prefs.contains(TimerWidget.TIMER_DURATION + widgetId) ||
            !prefs.contains(TimerWidget.TIMER_COLOR + widgetId))
            return null;

        // Extract the value of all fields
        String name = prefs.getString(TimerWidget.TIMER_NAME + widgetId, "");
        String icon = prefs.getString(TimerWidget.TIMER_ICON + widgetId, "");
        int duration = (int) prefs.getLong(TimerWidget.TIMER_DURATION + widgetId, 0) / 1000;
        int color = prefs.getInt(TimerWidget.TIMER_COLOR + widgetId, 0);

        // Construct and return the new Prefab_Timer object
        return new Prefab_Timer(name, icon, duration, color);
    }

    /**
     * Creates a timer prefab with default information.
     *
     * @return The newly constructed default Prefab_Timer.
     */
    public static Prefab_Timer buildDefault()
    {
        // Creates a new prefab with no name, the clock icon, five minute duration, and the color grey
        return new Prefab_Timer("", parseCodepoints(new String[]{"1F550"}), 300, Color.GRAY);
    }

    /**
     * Getter method for the name of this Prefab_Timer.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter method for the icon resource ID of this Prefab_Timer.
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Getter method for the duration of this Prefab_Timer.
     */
    @Override
    public int getDuration()
    {
        return duration;
    }

    /**
     * Getter method for the color of this Prefab_Timer.
     */
    public int getColor()
    {
        return color;
    }


    /**
     * @inheritDoc
     */
    @Override
    public Prefab copy()
    {
        return new Prefab_Timer(name, icon, duration, color);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void save(Context c, int widgetId)
    {
        // Ensure the icon string is sanitized before saving
        icon = sanitizeIconString(icon);

        // Get the shared preferences editor
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(c).edit();

        // Write all settings into shared preferences
        String id = Integer.toString(widgetId);
        edit.putString(TimerWidget.TIMER_NAME + id, name);
        edit.putString(TimerWidget.TIMER_ICON + id, icon);
        edit.putInt(TimerWidget.TIMER_COLOR + id, color);
        edit.putLong(TimerWidget.TIMER_DURATION + id, duration * 1000); // Seconds -> Milliseconds

        // Commit changes synchronously to ensure they are written before updating widgets
        edit.commit();
    }


    /**
     * @inheritDoc
     */
    @Override
    public int getConfigPreviewResource()
    {
        return R.layout.layout_prefabitem_timer;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void attachDataToConfigPreview(View groupView)
    {
        // Set the background color of the preview
        groupView.findViewById(R.id.PrefabItem_Background).setBackgroundColor(color);

        // Get the appropriate icon for the icon string, and apply it to the preview
        ImageView iconView = (ImageView) groupView.findViewById(R.id.PrefabItem_Icon);
        iconView.setImageResource(EmojiconHandler_Custom.getIcon(groupView.getContext(), icon));

        // Apply the name to the preview, and adjust its color based on the background color
        TextView label = (TextView) groupView.findViewById(R.id.PrefabItem_Label);
        label.setText(name);
        label.setTextColor(ColorUtils.getContrastColor(color));
    }

    /**
     * @inheritDoc
     */
    @Override
    public View getConfigView(Context c)
    {
        // Inflate the Prefab_Timer configuration view
        View view = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.dialog_config_prefabtimer, null);

        // Apply the name to the name's input field
        ((EditText) view.findViewById(R.id.PrefabTimer_Config_NameInput))
                .setText(name);
        // Apply the icon to the icon's input field
        ((EmojiPickerView) view.findViewById(R.id.PrefabTimer_Config_IconInput))
                .setIcon(icon);
        // Apply the color to the color's input field
        ColorSwitcherView cpv = (ColorSwitcherView) view.findViewById(R.id.PrefabTimer_Config_ColorInput);
        cpv.selectColor(color);
        // Apply the duration to the duration's input field
        ((DurationSetterView) view.findViewById(R.id.PrefabTimer_Config_DurationInput))
                .setTotalSeconds(duration);

        return view;
    }

    /**
     * @inheritDoc
     */
    @Override
    public EmojiPickerView identifyEmojiPickerView(View view)
    {
        // Retrieve the EmojiPickerView from the config view
        return (EmojiPickerView) view.findViewById(R.id.PrefabTimer_Config_IconInput);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void absorbConfigViewValues(View view)
    {
        // Get references to all input fields
        EditText nameInput = (EditText) view.findViewById(R.id.PrefabTimer_Config_NameInput);
        EmojiPickerView iconInput = (EmojiPickerView) view.findViewById(R.id.PrefabTimer_Config_IconInput);
        ColorSwitcherView colorInput = (ColorSwitcherView) view.findViewById(R.id.PrefabTimer_Config_ColorInput);
        DurationSetterView durationInput =
                (DurationSetterView) view.findViewById(R.id.PrefabTimer_Config_DurationInput);

        // Check if any fields were not found, for whatever reason, and abort if so
        if (nameInput == null || iconInput == null || colorInput == null || durationInput == null)
            return;

        // Apply the values of the input fields
        name = nameInput.getText().toString();
        icon = sanitizeIconString(iconInput.getIcon());
        color = colorInput.getSelected();
        duration = durationInput.getTotalSeconds();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void writeToJSON(JsonWriter writer) throws IOException
    {
        // Start JSON object
        writer.beginObject();

        // Write type field
        writer.name(FIELD_TYPE);
        writer.value(PREFAB_TIMER);

        // Write name field
        writer.name(FIELD_NAME);
        writer.value(name);

        // Get codepoint array for icon string
        icon = sanitizeIconString(icon);
        String[] iconStrings = codePointArray(icon);

        // Start JSON array and put add all codepoints
        writer.name(FIELD_ICON);
        writer.beginArray();
        for (String s : iconStrings)
            writer.value(s);
        writer.endArray();

        // Write duration field
        writer.name(FIELD_DURATION);
        writer.value(duration);

        // Write color field
        writer.name(FIELD_COLOR);
        writer.value("#" + Integer.toHexString(color));

        // End JSON object
        writer.endObject();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString()
    {
        return "Prefab_Timer{" +
               "name='" + name + '\'' +
               ", icon=" + Arrays.toString(codePointArray(icon)) +
               ", duration=" + duration +
               ", color=#" + Integer.toHexString(color) +
               '}';
    }

    // FOR PARCELLING


    /**
     * @inheritDoc
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeInt(color);
        dest.writeInt(duration);
    }

    // Object required by all Parcelables for reconstructing data from parcels
    public static final Parcelable.Creator<Prefab_Timer> CREATOR =
            new Parcelable.Creator<Prefab_Timer>()
            {
                public Prefab_Timer createFromParcel(Parcel in)
                {
                    return new Prefab_Timer(in);
                }

                public Prefab_Timer[] newArray(int size)
                {
                    return new Prefab_Timer[size];
                }
            };

    /**
     * Construct a Prefab_Timer object from a Parcel stream.
     *
     * @param in Parcel stream to read data from.
     */
    private Prefab_Timer(Parcel in)
    {
        name = in.readString();
        icon = in.readString();
        color = in.readInt();
        duration = in.readInt();
    }
}
