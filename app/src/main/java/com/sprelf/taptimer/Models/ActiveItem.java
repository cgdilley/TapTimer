package com.sprelf.taptimer.Models;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import com.sprelf.taptimer.Views.EmojiPickerView;

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

public abstract class ActiveItem implements Parcelable
{
    protected Prefab prefab;
    protected int widgetId;

    public Prefab getPrefab()
    {
        return prefab;
    }

    public void setPrefab(Prefab prefab)
    {
        this.prefab = prefab;
    }

    public int getWidgetId()
    {
        return widgetId;
    }

    /** Saves the current contents of this ActiveItem object into SharedPreferences.
     *
     * @param c Context within which to perform the operation
     */
    public abstract void save(Context c);

    /** Gets the resource identifier of the layout for rendering this Prefab object in the config
     * screen.
     *
     * @return Resource identifier for the rendering layout.
     */
    public abstract int getConfigLayoutResource();

    /**
     * Performs the work of manipulating the view based on the contents of this ActiveItem.
     *
     * @param view View to manipulate.
     */
    public abstract void attachDataToView(View view);

    /** Resets the values of this active item (eg. resetting the timer).
     *
     */
    public abstract void reset();

    public abstract View getConfigLayout(Context c);

    public abstract EmojiPickerView identifyEmojiPickerView(View view);

    public abstract void absorbConfigViewValues(View view);
}
