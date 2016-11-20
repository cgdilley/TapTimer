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

import com.rarepebble.colorpicker.ColorPickerView;
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

public class ColorSwitcherView extends FrameLayout
{
    // Relative weight of selected items in LinearLayout
    public static final float SELECTED_WEIGHT = 0.9f;

    // The currently selected color view
    private View selectedColor = null;

    // References to view resources
    private View customPicker;
    private ViewGroup pickerBar;


    public ColorSwitcherView(Context c)
    {
        super(c);
        initialize();
    }

    public ColorSwitcherView(Context c, AttributeSet as)
    {
        super(c, as);
        initialize();
    }

    /** Getter method for the color of the currently selected color view.
     */
    public int getSelected()
    {
        return getBackgroundColor(selectedColor);
    }

    /** Initializes the layout of the ColorPickerView.
     *
     */
    private void initialize()
    {
        // Inflate the ColorSwitcherView layout into this view
        inflate(getContext(), R.layout.view_colorpicker, this);

        // Get references to the custom color picker and the picker bar itself
        customPicker = findViewById(R.id.ColorPicker_CustomPicker);
        pickerBar = (ViewGroup) findViewById(R.id.ColorPicker_ColorBar);

        // Set the default custom color to be white
        changeCustomColor(Color.WHITE);

        // Apply the on-click listener to the custom color picker button
        customPicker.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                // Start dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // Inflate view to display inside dialog
                View dialogView = inflate(getContext(), R.layout.fragment_color_picker, null);

                // Get the color picker view in the inflated view
                final ColorPickerView pickerView =
                        (ColorPickerView) dialogView.findViewById(R.id.ColorPickerFragment_ColorPicker);

                // Set the color selected by the color picker pop-up
                pickerView.setColor(getBackgroundColor(v));

                // Set the background color and text color of the color picker pop-up's text field
                pickerView.findViewById(R.id.hexEdit).setBackgroundColor(Color.WHITE);
                ((TextView) pickerView.findViewById(R.id.hexEdit)).setTextColor(Color.BLACK);

                // Construct the dialog and display it
                builder.setTitle(R.string.ColorPicker_Dialog_Title)
                       .setView(dialogView)
                       .setPositiveButton(R.string.ColorPicker_Dialog_Confirm,
                                          new DialogInterface.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which)
                                              {
                                                  // When confirm is clicked, apply the color
                                                  // selected by the color picker pop-up
                                                  changeCustomColor(pickerView.getColor());
                                                  selectView(customPicker);
                                                  // Force view update
                                                  invalidate();
                                              }
                                          })
                       .show();
            }
        });
        // Iterate through all views in the picker bar that are not the custom color picker
        for (int i = 0; i < pickerBar.getChildCount(); i++)
        {
            View view = pickerBar.getChildAt(i);

            if (view == customPicker)
                continue;

            // Apply an on-click listener that selects the view that was clicked
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

    /** Sets the given view as the view selected by this ColorSwitcherView.
     *
     * @param view The view to select
     */
    private void selectView(View view)
    {
        // Iterate through all views in the picker bar
        for (int i = 0; i < pickerBar.getChildCount(); i++)
        {
            View v = pickerBar.getChildAt(i);

            // If this view is the one to be selected
            if (view == v)
            {
                // Apply the layout weight to make it appear bigger horizontally
                TableRow.LayoutParams lp =
                        new TableRow.LayoutParams(LayoutParams.FILL_PARENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT,
                                                  SELECTED_WEIGHT);

                // Remove margins to make it appear bigger vertically
                lp.setMargins(0, 0, 0, 0);
                v.setLayoutParams(lp);
            }
            // Otherwise, if this view is not the one selected
            else
            {
                // Apply the default layout weight
                TableRow.LayoutParams lp =
                        new TableRow.LayoutParams(LayoutParams.FILL_PARENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT,
                                                  1f);

                // Apply the default margins
                int margin = getResources().getDimensionPixelSize(R.dimen.ColorPicker_UnselectedMargin);
                lp.setMargins(0, margin, 0, margin);
                v.setLayoutParams(lp);
            }
        }

        // Store the selected view
        selectedColor = view;

        // Force a view update
        invalidate();
    }

    /** Gets the background color of the given view.
     *
     * @param view The view to get the background color of.
     * @return The background color of the given view.
     */
    private int getBackgroundColor(View view)
    {
        return ((ColorDrawable) view.getBackground()).getColor();
    }

    /** Changes the color currently shown by the custom color picker view to the given color.
     *
     * @param color The color to set.
     */
    public void changeCustomColor(int color)
    {
        // Calculate the appropriate contrast color for the border and the text
        int contrastColor = getContrastColor(color);

        // Set the background color
        customPicker.setBackgroundColor(color);

        // Construct the border drawable and apply the appropriate contrast color
        View borderView = findViewById(R.id.ColorPicker_CustomBorder);
        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setStroke(getResources().getDimensionPixelSize(R.dimen.ColorPicker_CustomPickerBorder),
                         contrastColor);
        borderView.setBackground(border);

        // Set the appropriate contrasting color to the text
        TextView textView = (TextView) findViewById(R.id.ColorPicker_CustomText);
        textView.setTextColor(contrastColor);
    }

    /** Selects the view that represents the given color.  If no view represents the given color,
     * the custom color picker view is changed to match it.
     *
     * @param color The color to select.
     */
    public void selectColor(int color)
    {
        // Iterate through all views in the picker bar
        for (int i = 0; i < pickerBar.getChildCount(); i++)
        {
            View v = pickerBar.getChildAt(i);

            // If the background color of this view matches the given color, select this view
            // and return out of this method.
            if (getBackgroundColor(v) == color)
            {
                selectView(v);
                return;
            }
        }

        // If no view was selected, change the custom color and select it.
        changeCustomColor(color);
        selectView(customPicker);
    }

    /** Calculates the color that contrasts the given color (either black or white).
     *
     * @param colorToContrast Color to find the contrast of.
     * @return Returns black if the given color's luminance is greater than 0.5, white otherwise.
     */
    public static int getContrastColor(int colorToContrast)
    {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorToContrast), Color.green(colorToContrast),
                       Color.blue(colorToContrast), hsv);
        return hsv[2] > 0.5f ? Color.BLACK : Color.WHITE;
    }
}
