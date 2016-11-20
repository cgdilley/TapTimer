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

public abstract class ActiveItem implements Parcelable, Configurable
{
    protected Prefab prefab;
    protected int widgetId;

    /** Getter method for this ActiveItem's prefab field.
     */
    public Prefab getPrefab()
    {
        return prefab;
    }

    /** Setter method for this ActiveItem's prefab field.
     */
    public void setPrefab(Prefab prefab)
    {
        this.prefab = prefab;
    }

    /** Getter method for this ActiveItem's widget ID field.
     */
    public int getWidgetId()
    {
        return widgetId;
    }


    //// ABSTRACT METHODS

    /** Saves the current contents of this ActiveItem object into SharedPreferences.
     *
     * @param c Context within which to perform the operation
     */
    public abstract void save(Context c);


    /** Resets the values of this active item (eg. resetting the timer).
     *
     */
    public abstract void reset();

    /** @inheritDoc
     */
    public abstract int getConfigPreviewResource();

    /** @inheritDoc
     */
    public abstract void attachDataToConfigPreview(View view);

    /** @inheritDoc
     */
    public abstract View getConfigView(Context c);

    /** @inheritDoc
     */
    public abstract EmojiPickerView identifyEmojiPickerView(View view);

    /** @inheritDoc
     */
    public abstract void absorbConfigViewValues(View view);
}
