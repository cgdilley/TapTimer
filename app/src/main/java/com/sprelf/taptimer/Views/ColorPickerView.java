package com.sprelf.taptimer.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableRow;
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

public class ColorPickerView extends FrameLayout
{
    public static final float SELECTED_WEIGHT = 0.9f;

    private View selectedColor = null;

    private View customPicker;
    private ViewGroup pickerBar;


    public ColorPickerView(Context c)
    {
        super(c);
        initialize();
    }

    public ColorPickerView(Context c, AttributeSet as)
    {
        super(c, as);
        initialize();
    }

    public int getSelected()
    {
        return getBackgroundColor(selectedColor);
    }

    private void initialize()
    {
        inflate(getContext(), R.layout.view_colorpicker, this);

        customPicker = findViewById(R.id.ColorPicker_CustomPicker);
        pickerBar = (ViewGroup) findViewById(R.id.ColorPicker_ColorBar);

        changeCustomColor(Color.WHITE);

        customPicker.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = inflate(getContext(), R.layout.fragment_color_picker, null);

                final com.rarepebble.colorpicker.ColorPickerView pickerView =
                        (com.rarepebble.colorpicker.ColorPickerView)
                                dialogView.findViewById(R.id.ColorPickerFragment_ColorPicker);

                pickerView.setColor(getBackgroundColor(v));

                pickerView.findViewById(R.id.hexEdit).setBackgroundColor(Color.WHITE);
                ((TextView) pickerView.findViewById(R.id.hexEdit)).setTextColor(Color.BLACK);
                builder.setTitle(R.string.ColorPicker_Dialog_Title)
                       .setView(dialogView)
                       .setPositiveButton(R.string.ColorPicker_Dialog_Confirm,
                                          new DialogInterface.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which)
                                              {
                                                  changeCustomColor(pickerView.getColor());
                                                  selectView(customPicker);
                                                  invalidate();
                                              }
                                          })
                       .show();
            }
        });
        for (int i = 0; i < pickerBar.getChildCount(); i++)
        {
            View view = pickerBar.getChildAt(i);

            if (view == customPicker)
                continue;

            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectView(v);
                }
            });
        }
    }

    private void selectView(View view)
    {
        for (int i = 0; i < pickerBar.getChildCount(); i++)
        {
            View v = pickerBar.getChildAt(i);

            if (view == v)
            {
                TableRow.LayoutParams lp =
                        new TableRow.LayoutParams(LayoutParams.FILL_PARENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT,
                                                  SELECTED_WEIGHT);
                lp.setMargins(0, 0, 0, 0);
                v.setLayoutParams(lp);
            }
            else
            {
                TableRow.LayoutParams lp =
                        new TableRow.LayoutParams(LayoutParams.FILL_PARENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT,
                                                  1f);
                int margin = getResources().getDimensionPixelSize(R.dimen.ColorPicker_UnselectedMargin);
                lp.setMargins(0, margin, 0, margin);
                v.setLayoutParams(lp);
            }
        }

        selectedColor = view;

        invalidate();
    }

    private int getBackgroundColor(View view)
    {
        return ((ColorDrawable) view.getBackground()).getColor();
    }

    public void changeCustomColor(int color)
    {
        int contrastColor = getContrastColor(color);

        customPicker.setBackgroundColor(color);

        View borderView = findViewById(R.id.ColorPicker_CustomBorder);
        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setStroke(getResources().getDimensionPixelSize(R.dimen.ColorPicker_CustomPickerBorder),
                         contrastColor);
        borderView.setBackground(border);


        TextView textView = (TextView) findViewById(R.id.ColorPicker_CustomText);
        textView.setTextColor(contrastColor);
    }

    public void setCustomColorAsSelected()
    {
        selectView(customPicker);
        invalidate();
    }

    public static int getContrastColor(int colorToContrast)
    {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorToContrast), Color.green(colorToContrast),
                       Color.blue(colorToContrast), hsv);
        return hsv[2] > 0.5f ? Color.BLACK : Color.WHITE;
    }
}
