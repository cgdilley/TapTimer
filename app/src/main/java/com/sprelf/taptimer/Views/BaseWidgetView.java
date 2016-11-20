package com.sprelf.taptimer.Views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

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
 * Created by Chris on 12.01.2016.
 * THIS WAS CREATED FOR THE FLIGHT BATTERY APP, MODIFIED TO SUIT THE NEEDS OF THIS APP
 * Describes how widget view for this app should be structured.
 */
abstract public class BaseWidgetView extends View
{
    protected String widgetId;

    // Percentage that should be displayed by the widget
    protected float percentage;
    // Whether or not the widget should fade when inactive
    protected boolean fadeIfInactive = true;

    public BaseWidgetView(Context c)
    {
        super(c);
    }

    public BaseWidgetView(Context c, AttributeSet as) { super(c, as); }


    /** Setter method for this view's associated widget ID.  Converts the given integer into a String.
     *
     * @param value The widget ID to set.
     */
    public void setWidgetId(int value) { widgetId = Integer.toString(value); }

    /** Setter method for this view's associated widget ID.
     *
     * @param value The widget ID to set, as a String.
     */
    public void setWidgetId(String value) { widgetId = value; }

    /** Setter method for this view's percentage to display.
     *
     * @param percentage The percentage value to display.
     */
    public void setPercentage(float percentage)
    {
        this.percentage = percentage;
    }

    /** Setter method for whether this view should fade if it is determined to be inactive or not.
     *
     * @param fadeIfInactive True if this view should fade if inactive, false otherwise.
     */
    public void setFadeIfInactive(boolean fadeIfInactive)
    {
        this.fadeIfInactive = fadeIfInactive;
    }

    /** Calculates the average of the two given colors, with the second value weighted by the given
     * weight between 0 and 1.
     *
     * @param color1 First color to average (is multiplied by 1 minus the weight)
     * @param color2 Second color to average (is multiplied by weight)
     * @param weight The weight of the second color
     * @return The weighted average of the two colors
     */
    protected static int averageColor(int color1, int color2, float weight)
    {
        int red = (int) ((Color.red(color1) * (1 - weight)) + (Color.red(color2) * weight));
        int green = (int) ((Color.green(color1) * (1 - weight)) + (Color.green(color2) * weight));
        int blue = (int) ((Color.blue(color1) * (1 - weight)) + (Color.blue(color2) * weight));
        int alpha = (int) ((Color.alpha(color1) * (1 - weight)) + (Color.alpha(color2) * weight));
        return Color.argb(alpha, red, green, blue);
    }

}
