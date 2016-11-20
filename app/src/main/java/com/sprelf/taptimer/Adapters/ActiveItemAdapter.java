package com.sprelf.taptimer.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sprelf.taptimer.Models.ActiveItem;
import com.sprelf.taptimer.R;

import java.util.List;

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
 * Used to display {@link ActiveItem} members of a list in a list view.
 */

public class ActiveItemAdapter extends ArrayAdapter<ActiveItem>
{
    private Context c;
    private List<ActiveItem> objects;

    /** Constructs a new ActiveTimer adapter.
     *
     * @param c Context within which to perform the operation.
     * @param objects List of {@link ActiveItem} objects to render.
     */
    public ActiveItemAdapter(Context c, List<ActiveItem> objects)
    {
        super(c, R.layout.layout_activeitem_placeholder, objects);

        this.c = c;
        this.objects = objects;
    }

    /** @inheritDoc
    * For ActiveItemAdapter, inflates the active item view and applies the active timer data.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the layout inflater service
        LayoutInflater inflater =
                (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the active item associated with the current position
        ActiveItem activeItem = objects.get(position);

        // Inflate the layout resource associated with this active item
        View returnView = inflater.inflate(activeItem.getConfigPreviewResource(), parent, false);

        // Have the active item fill in its values into the inflated view
        activeItem.attachDataToConfigPreview(returnView);

        // Return the inflated view
        return returnView;
    }


}
