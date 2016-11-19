package com.sprelf.taptimer.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.sprelf.taptimer.Activities.EmojiPickerActivity;
import com.sprelf.taptimer.Emojicon.EmojiconHandler_Custom;
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
public class EmojiPickerView extends FrameLayout
{
    public static final int RESULT_EMOJI_PICKED = 101;
    public static final String EMOJI_EXTRA = "EmojiExtra";

    private ImageButton button;
    private String icon;
    private Activity activity;

    public EmojiPickerView(Context c)
    {
        super(c);
        initialize();
    }

    public EmojiPickerView(Context c, AttributeSet as)
    {
        super(c, as);
        initialize();
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
        if (button != null)
            button.setImageResource(EmojiconHandler_Custom.getIcon(getContext(), icon));
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    private void initialize()
    {
        inflate(getContext(), R.layout.view_emojipicker, this);

        button = (ImageButton) findViewById(R.id.EmojiPicker_Button);

        button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activity != null)
                {
                    Intent intent = new Intent(getContext(), EmojiPickerActivity.class);
                    activity.startActivityForResult(intent, RESULT_EMOJI_PICKED);
                }
            }
        });


    }
}
