package com.sprelf.taptimer.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sprelf.taptimer.R;

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
 * Created by Chris on 17.11.2016.
 * Class for managing multiple input boxes for inputting an amount of time in hours, minutes, and
 * seconds.
 */
public class DurationSetterView extends FrameLayout
{
    // References to the input boxes
    EditText hours, minutes, seconds;

    // Value of the total number of seconds represented by this view
    int totalSeconds = 0;

    public DurationSetterView(Context context)
    {
        super(context);
        initialize();
    }

    public DurationSetterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public DurationSetterView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    /** Getter method for the total number of seconds represented by this view.
     */
    public int getTotalSeconds()
    {
        return totalSeconds;
    }

    /** Setter method for the total number of seconds represented by this view.
     */
    public void setTotalSeconds(int totalSeconds)
    {
        this.totalSeconds = totalSeconds;
        recalculateViews();
    }

    /**
     * Initializes the layout of the DurationSetterView.
     */
    private void initialize()
    {
        // Inflate the duration setter layout
        inflate(getContext(), R.layout.view_durationsetter, this);

        // Get references to all input boxes
        hours = (EditText)findViewById(R.id.DurationSetter_HourInput);
        minutes = (EditText)findViewById(R.id.DurationSetter_MinuteInput);
        seconds = (EditText)findViewById(R.id.DurationSetter_SecondInput);

        // Construct a focus change listener to apply to all input boxes
        OnFocusChangeListener focusChangeListener = new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                // If for some reason this is not a text view, do nothing
                if (!(v instanceof TextView))
                    return;

                // If the view is gaining focus, set its text to blank
                if (hasFocus)
                    ((TextView)v).setText("");
                // Otherwise, if it is losing focus
                else
                {
                    // If the view is still empty, restore its previous value by simply
                    // recalculating the views based on a total seconds value that is not changed
                    if (((TextView)v).getText().toString().equals(""))
                        recalculateViews();
                    // Otherwise, recalculate the total seconds value based on the new input
                    else
                        recalculate();
                }
            }
        };

        // Construct an editor action listener to apply to all input boxes
        TextView.OnEditorActionListener editorActionListener =
                new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                    {
                        // If the 'Done' action occurs (by pressing enter button or key)
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                        {
                            // Recalculate the total seconds value of the view based on input
                            recalculate();
                        }
                        return false;
                    }
                };


        // Apply both listeners to all input boxes

        hours.setOnFocusChangeListener(focusChangeListener);
        minutes.setOnFocusChangeListener(focusChangeListener);
        seconds.setOnFocusChangeListener(focusChangeListener);

        hours.setOnEditorActionListener(editorActionListener);
        minutes.setOnEditorActionListener(editorActionListener);
        seconds.setOnEditorActionListener(editorActionListener);

    }

    /** Recalculates the total seconds value of this view by reading the values of all input boxes.
     * Then recalculates what should be displayed in those boxes based on this calculated value,
     * allowing overflow of minutes and seconds to be corrected.
     */
    private void recalculate()
    {
        // If the views are not properly referenced, abort
        if (hours == null || minutes == null || seconds == null)
            return;

        // Calculate the new value of seconds based on the values in the input boxes
        int newValue = (extractValue(hours) * (60*60)) +
                       (extractValue(minutes) * 60) +
                       (extractValue(seconds));
        // If the value is different than what was previously stored, force the views to recalculate
        // what to display based on this new seconds total
        if (newValue != totalSeconds)
        {
            totalSeconds = newValue;
            recalculateViews();
        }
    }

    /** Changes the values of all input boxes to represent the total seconds of this view.
     *
     */
    private void recalculateViews()
    {
        // If the views are not properly referenced, abort
        if (hours == null || minutes == null || seconds == null)
            return;

        // Calculate hours, minutes, and seconds
        hours.setText(Integer.toString(getHours()));
        minutes.setText(Integer.toString(getMinutes()));
        seconds.setText(Integer.toString(getSeconds()));

        // Force update of the view
        invalidate();
    }

    /** Calculates the number of hours based on the total seconds of this view, rounded down to
     * the nearest integer.
     *
     * @return The number of hours.
     */
    private int getHours()
    {
        return totalSeconds / (60*60);
    }

    /** Calculates the number of minutes based on the total seconds of this view, rounded down to
     * the nearest integer.
     *
     * @return The number of minutes.
     */
    private int getMinutes()
    {
        return (totalSeconds % (60*60)) / 60;
    }

    /** Calculates the number of seconds based on the total seconds of this view, rounded down to
     * the nearest integer.
     *
     * @return The number of seconds.
     */
    private int getSeconds()
    {
        return totalSeconds % 60;
    }

    /** Extracts the integer value of the given TextView.  If an error occurs while doing so,
     * returns 0 instead.
     *
     * @param view View to extract an integer value from.
     * @return The extracted integer value, or 0 if extraction failed.
     */
    private int extractValue(TextView view)
    {
        try
        {
            return Integer.parseInt(view.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
