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
 */

public class DurationSetterView extends FrameLayout
{
    EditText hours, minutes, seconds;

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

    public int getTotalSeconds()
    {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds)
    {
        this.totalSeconds = totalSeconds;
        recalculateViews();
    }

    private void initialize()
    {
        inflate(getContext(), R.layout.view_durationsetter, this);

        hours = (EditText)findViewById(R.id.DurationSetter_HourInput);
        minutes = (EditText)findViewById(R.id.DurationSetter_MinuteInput);
        seconds = (EditText)findViewById(R.id.DurationSetter_SecondInput);

        OnFocusChangeListener focusChangeListener = new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!(v instanceof TextView))
                    return;

                if (hasFocus)
                    ((TextView)v).setText("");
                else
                {
                    if (((TextView)v).getText().toString().equals(""))
                        recalculateViews();
                    else
                        recalculate();
                }
            }
        };


        TextView.OnEditorActionListener editorActionListener =
                new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                    {
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                        {
                            recalculate();
                        }
                        return false;
                    }
                };


        hours.setOnFocusChangeListener(focusChangeListener);
        minutes.setOnFocusChangeListener(focusChangeListener);
        seconds.setOnFocusChangeListener(focusChangeListener);

        hours.setOnEditorActionListener(editorActionListener);
        minutes.setOnEditorActionListener(editorActionListener);
        seconds.setOnEditorActionListener(editorActionListener);

    }

    private void recalculate()
    {
        if (hours == null || minutes == null || seconds == null)
            return;

        int newValue = (extractValue(hours) * (60*60)) +
                       (extractValue(minutes) * 60) +
                       (extractValue(seconds));
        if (newValue != totalSeconds)
        {
            totalSeconds = newValue;
            recalculateViews();
        }
    }

    private void recalculateViews()
    {
        if (hours == null || minutes == null || seconds == null)
            return;

        hours.setText(Integer.toString(getHours()));
        minutes.setText(Integer.toString(getMinutes()));
        seconds.setText(Integer.toString(getSeconds()));
        invalidate();
    }

    private int getHours()
    {
        return totalSeconds / (60*60);
    }

    private int getMinutes()
    {
        return (totalSeconds % (60*60)) / 60;
    }

    private int getSeconds()
    {
        return totalSeconds % 60;
    }

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
