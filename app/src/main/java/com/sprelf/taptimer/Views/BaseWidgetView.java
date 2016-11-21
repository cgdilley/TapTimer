package com.sprelf.taptimer.Views;

import android.content.Context;
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


}
