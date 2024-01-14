package com.sprelf.taptimer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sprelf.taptimer.Models.Prefab;
import com.sprelf.taptimer.Models.Prefab_Timer;
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
 * Used to display {@link Prefab_Timer} members of a list in a list view.
 */

public class PrefabAdapter extends ArrayAdapter<Prefab>
{
    private Context c;
    private List<Prefab> objects;

    /**
     * Constructs a new Prefab adapter.
     *
     * @param c       Context within which to perform the operation.
     * @param objects List of {@link Prefab_Timer} objects to render.
     */
    public PrefabAdapter(Context c, List<Prefab> objects)
    {
        super(c, R.layout.layout_prefabitem_placeholder, objects);

        this.c = c;
        this.objects = objects;
    }

    /**
     * @inheritDoc For PrefabAdapter, inflates the prefab item view and applies the prefab data.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the layout inflater service
        LayoutInflater inflater =
                (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View returnView;
        // If we're getting any view other than the last element in the list,
        // inflate the layout resource for the prefab and have the prefab apply its values to it
        if (position < objects.size() - 1)
        {

            Prefab prefab = objects.get(position);
            returnView = inflater.inflate(prefab.getConfigPreviewResource(), parent, false);
            prefab.attachDataToConfigPreview(returnView);
        }
        // Otherwise, if we're looking at the final element, inflate the 'add new' layout
        else
        {
            returnView = inflater.inflate(R.layout.layout_prefab_addnew, parent, false);
        }

        // Return the inflated view
        return returnView;

    }



}
