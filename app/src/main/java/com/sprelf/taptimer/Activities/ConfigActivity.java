package com.sprelf.taptimer.Activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.sprelf.taptimer.Adapters.ActiveItemAdapter;
import com.sprelf.taptimer.Adapters.PrefabAdapter;
import com.sprelf.taptimer.Models.ActiveItem;
import com.sprelf.taptimer.Models.ActiveTimer;
import com.sprelf.taptimer.Models.Prefab;
import com.sprelf.taptimer.Models.Prefab_Timer;
import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Utils.JSONUtils;
import com.sprelf.taptimer.Widgets.TimerWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
 * ConfigActivity is the primary activity for the app, appearing both as the launch activity,
 * and as the configuration activity when placing a new widget.  This activity displays all active
 * timers and all prefabs, and allows users to manipulate both.
 */
public class ConfigActivity extends Activity
{
    // Location of the prefab JSON file
    public static final String PREFABS_FILE = "timers.json";
    // Request code to send when opening a PropertyConfigActivity for editing a Prefab
    public static final int PREFAB_CONFIG_CODE = 1;
    // Request code to send when opening a PropertyConfigActivity for editing an ActiveItem
    public static final int ACTIVEITEM_CONFIG_CODE = 2;

    // The widget ID that may have been passed if this activity was launched in
    // response to the placement of a new widget
    private int mAppWidgetId;
    // List of all prefabs
    private List<Prefab> prefabList;
    // List of all active items
    private List<ActiveItem> activeItemList;
    // Variable for tracking the position of an element being modified, either when editing an
    // element from the active item list or the prefab list.
    private int modifyingPosition = ListView.INVALID_POSITION;

    // List view which displays the active items
    private ListView activeItemListView;
    // Grid view which displays the prefabs
    private GridView prefabGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);


        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        else
        {
            mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        }

        // Initialize all views in the activity
        initializeWindow();
    }

    /**
     * Perform all actions necessary to initialize the layout.
     */
    private void initializeWindow()
    {
        // Get the list of active timers and prefabs.
        prefabList = loadPrefabs();
        activeItemList = loadActiveItems();
        if (prefabList == null)
        {
            finish();
            return;
        }

        // Build the list view for the active timers  ****
        // Find the list view
        activeItemListView = (ListView) findViewById(R.id.Config_ActiveList);
        // Apply the active item adapter to the list view, using the active items list
        activeItemListView.setAdapter(new ActiveItemAdapter(this, activeItemList));
        // Set the listener for long-click events on individual items in the list view
        activeItemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Get the selected active item
                ActiveItem selected = activeItemList.get(position);
                // Store the position, in order to allow modification of the list once returning
                // from the PropertyConfigActivity.
                modifyingPosition = position;

                // Create intent to start the PropertyConfigActivity, passing along the
                // selected active item, and then start the activity.
                Intent intent = new Intent(getApplicationContext(), PropertyConfigActivity.class);
                intent.putExtra(PropertyConfigActivity.ACTIVEITEM_EXTRA, selected);
                startActivityForResult(intent, ACTIVEITEM_CONFIG_CODE);

                // Consume the touch event
                return true;
            }
        });

        // Build the list (grid) view for the prefabs *****
        // Find the grid view
        prefabGrid = (GridView) findViewById(R.id.Config_PrefabList);
        // Apply the prefab adapter to the grid view, using the prefabs list
        prefabGrid.setAdapter(new PrefabAdapter(this, prefabList));
        // Set the listener for on-click events on individual items in the view
        prefabGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // If the last item in the list was tapped (the add button)
                if (position == prefabList.size() - 1)
                {
                    // Store the position, in order to allow modification of the list once returning
                    // from the PropertyConfigActivity.
                    modifyingPosition = position;

                    // Create a new Prefab_Timer object...
                    // TODO:  MAKE MORE FLEXIBLE, able to add different types of prefabs
                    Prefab selected = Prefab_Timer.buildDefault();

                    // Create intent to start the PropertyConfigActivity, passing along the
                    // selected prefab, and then start the activity.
                    Intent intent = new Intent(getApplicationContext(), PropertyConfigActivity.class);
                    intent.putExtra(PropertyConfigActivity.PREFAB_EXTRA, selected);
                    startActivityForResult(intent, PREFAB_CONFIG_CODE);

                    // Stop handling the click event
                    return;
                }

                // When a prefab is clicked, its settings are applied to any selected
                // active timer
                int activePos = activeItemListView.getCheckedItemPosition();
                if (activePos != AdapterView.INVALID_POSITION)
                {
                    // Get the currently selected active timer
                    ActiveItem activeItem = activeItemList.get(activePos);
                    // Apply a copy of the prefab's info to the active timer
                    activeItem.setPrefab(prefabList.get(position).copy());

                    // Reset the timer
                    activeItem.reset();

                    // Save the values of the timer after modifying
                    activeItem.save(getApplicationContext());

                    Log.d("[WidgetConfig]", "Prefab selected: " + prefabList.get(position));

                    // Force an update of the active item list
                    ((ActiveItemAdapter) activeItemListView.getAdapter()).notifyDataSetChanged();
                }
            }
        });
        // Set the listener for long-click events on individual items in the grid view
        prefabGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                // If the last item in the list was long-clicked, ignore it
                if (position == prefabList.size() - 1)
                    return false;

                // Store the position, in order to allow modification of the list once returning
                // from the PropertyConfigActivity.
                modifyingPosition = position;
                // Get the prefab at the selected position
                Prefab selected = prefabList.get(position);

                // Create intent to start the PropertyConfigActivity, passing along the
                // selected prefab, and then start the activity.
                Intent intent = new Intent(getApplicationContext(), PropertyConfigActivity.class);
                intent.putExtra(PropertyConfigActivity.PREFAB_EXTRA, selected);
                startActivityForResult(intent, PREFAB_CONFIG_CODE);

                // Consume the touch event, so that it doesn't force the settings onto the
                // selected timer (overriding the onClick)
                return true;
            }
        });
    }

    /**
     * Loads all prefabs, and returns them as a list.
     *
     * @return List of all stored prefabs.  If loading failed, returns null.
     */
    private List<Prefab> loadPrefabs()
    {
        List<Prefab> returnList = new ArrayList<>();

        try
        {
            // Get the root JSON object of the JSON file.
            JSONObject obj = JSONUtils.loadJSONFile(this, PREFABS_FILE);
            if (obj == null)
                return null;

            // Get the array of prefabs
            JSONArray arr = obj.getJSONArray(Prefab.FIELD_TIMERS);

            // Iterate through the entire array and build Prefab representations of the prefabs
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject info = arr.getJSONObject(i);

                // Get the type of prefab to build
                String type = info.getString(Prefab.FIELD_TYPE);
                switch (type)
                {
                    case Prefab.PREFAB_TIMER:
                        returnList.add(Prefab_Timer.build(info));
                        break;

                    /*/ FOR ADDITIONAL WIDGET TYPES, PREFABS SHOULD BE BUILD HERE IN THE BY
                    /// ADDING TO THIS SWITCH STATEMENT

                    case <string identifier for prefab class>:
                        returnList.add(<Constructor method for prefab class>);
                        break;

                    /// ***************************************************************/
                }

            }

        } catch (JSONException e)
        {
            Log.e("[Config]", "Error while loading JSON file:  " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        // Add a null item at the end of the list as a placeholder for the add button
        returnList.add(null);
        return returnList;

    }

    /**
     * Generates a list of all active timers, returning them as a list.
     *
     * @return List of all active timers.
     */
    private List<ActiveItem> loadActiveItems()
    {
        List<ActiveItem> returnList = new ArrayList<>();

        // Iterate through the list of active widget IDs of the TimerWidget class
        for (int id : AppWidgetManager.getInstance(this)
                                      .getAppWidgetIds(new ComponentName(this, TimerWidget.class)))
        {
            // Construct an ActiveTimer representation of the active timer
            Prefab_Timer pt = Prefab_Timer.build(this, id);
            ActiveTimer at = ActiveTimer.build(this, id, pt);
            // If the data does not exist to create the active timer object, build a default timer
            // and save its values under this widget ID
            if (at == null)
            {
                at = ActiveTimer.buildNew(id, Prefab_Timer.buildDefault());
                if (at != null)
                    at.save(this);
            }

            // Add the ActiveTimer to the list
            returnList.add(at);
        }

        /*/ FOR ADDITIONAL WIDGET TYPES, ACTIVE ITEMS SHOULD BE BUILT HERE IN THE SAME MANNER
        /// AS THE ABOVE FOR-LOOP

        for (int id : AppWidgetManager.getInstance(this)
                                      .getAppWidgetIds(new ComponentName(this, <WIDGET CLASS NAME>)))
        {
            <PREFAB CLASS> pt = <CONSTRUCTOR FOR PREFAB>
            <ACTIVEITEM CLASS> at = <CONSTRUCTOR FOR ACTIVE ITEM>
            if (at == null)
            {
                at = <CONSTRUCT BLANK/DEFAULT ACTIVEITEM AND PREFAB>
                if (at != null)
                    at.save(this);
            }
            returnList.add(at);
        }
        /// ***************************************************************/

        // Return the assembled list
        return returnList;
    }

    /**
     * Called when the Confirm button is clicked.
     *
     * @param view The clicked view.
     */
    public void onConfirmClick(View view)
    {
        closeWithSuccess();
    }

    /**
     * Called when the Cancel button is clicked.
     *
     * @param view The clicked view.
     */
    public void onCancelClick(View view)
    {
        closeWithFailure();
    }

    /**
     * Closes this activity, reporting success.  If this activity was called as a response to
     * placing a widget, this indicates that the widget may go ahead and be placed.
     */
    private void closeWithSuccess()
    {
        savePrefabsToJSON();
        for (ActiveItem ai : activeItemList)
            ai.save(this);

        Intent update = new Intent(TimerWidget.ACTION_AUTO_UPDATE);
        sendBroadcast(update);

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * Closes this activity, reporting failure.  If this activity was called as a response to
     * placing a widget, this indicates that the widget should not be placed.
     */
    private void closeWithFailure()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * @inheritDoc For ConfigActivity, handles the following request codes:
     * PREFAB_CONFIG_CODE:  Modification of a Prefab in the prefabs list
     * ACTIVEITEM_CONFIG_CODE:  Modification of an ActiveItem in the active items list
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // If we receive a successful result code and the returned intent is carrying data
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
        {

            switch (requestCode)
            {
                // If this activity is returning from modifying a prefab
                case PREFAB_CONFIG_CODE:

                    // If the stored modifying position is valid
                    if (modifyingPosition != ListView.INVALID_POSITION)
                    {
                        // If the selected prefab was deleted
                        if (data.getExtras().containsKey(PropertyConfigActivity.DELETE_PREFAB_EXTRA))
                        {
                            // Remove the item from the list and update the grid view
                            prefabList.remove(modifyingPosition);
                            ((PrefabAdapter) prefabGrid.getAdapter())
                                    .notifyDataSetChanged();
                        }
                        // Otherwise, if the intent is carrying prefab data
                        else if (data.getExtras().containsKey(PropertyConfigActivity.PREFAB_EXTRA))
                        {
                            // Extract the prefab data
                            Prefab newPrefab = data.getExtras().getParcelable(
                                    PropertyConfigActivity.PREFAB_EXTRA);

                            // If the data extraction didn't fail
                            if (newPrefab != null)
                            {
                                // Change the item at the modifying position to this new prefab
                                prefabList.set(modifyingPosition, newPrefab);

                                // If it was the last item of the list that was modified, then
                                // this above replacement overwrote the final 'null' prefab.  So,
                                // we must add a new null prefab at the end of the list
                                if (modifyingPosition == prefabList.size() - 1)
                                    prefabList.add(null);

                                // Update the prefab grid view
                                ((PrefabAdapter) prefabGrid.getAdapter())
                                        .notifyDataSetChanged();
                            }
                        }
                    }
                    break;

                // If this activity is returning from modifying an active item
                case ACTIVEITEM_CONFIG_CODE:

                    // If the modifying position is valid and contains active item data
                    if (modifyingPosition != AdapterView.INVALID_POSITION &&
                        data.getExtras().containsKey(PropertyConfigActivity.ACTIVEITEM_EXTRA))
                    {
                        // Extract the active item data from the given intent
                        ActiveItem newItem = data.getExtras().getParcelable(
                                PropertyConfigActivity.ACTIVEITEM_EXTRA);

                        // If the data extraction didn't fail
                        if (newItem != null)
                        {
                            // Save the new item
                            newItem.save(this);
                            // Set the active item at the modifying position to this new active item
                            activeItemList.set(modifyingPosition, newItem);

                            // Update the active item list view
                            ((ActiveItemAdapter) activeItemListView.getAdapter())
                                    .notifyDataSetChanged();
                        }
                    }
                    break;

                // If the request code is unknown, simply do default behavior
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
        // Otherwise, perform default behavior
        else
            super.onActivityResult(requestCode, resultCode, data);

        // Reset the modifying position
        modifyingPosition = ListView.INVALID_POSITION;
    }

    /**
     * Commits all changes to the prefabs list to a local JSON file.
     */
    private void savePrefabsToJSON()
    {
        if (prefabList == null)
            return;

        try
        {
            Log.d("[Config]", "Saving prefab data...");

            // Open the json file writer
            JsonWriter writer =
                    new JsonWriter(new OutputStreamWriter(openFileOutput(PREFABS_FILE,
                                                                         Context.MODE_PRIVATE),
                                                          "UTF-8"));
            writer.setIndent("  ");

            // Add the base object
            writer.beginObject();
            // Add the initial field for holding the array
            writer.name(Prefab.FIELD_TIMERS);
            writer.beginArray();

            // Iterate through all prefabs, and have them add their data to the JSON writer
            for (Prefab prefab : prefabList)
                if (prefab != null)
                    prefab.writeToJSON(writer);

            writer.endArray();
            writer.endObject();

            writer.close();
        } catch (IOException e)
        {
            Log.e("[Config]", "Error writing to JSON file: " + e.getMessage());
        }
    }
}
