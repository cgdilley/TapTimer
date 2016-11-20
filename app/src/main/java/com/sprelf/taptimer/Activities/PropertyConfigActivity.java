package com.sprelf.taptimer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.sprelf.taptimer.Models.ActiveItem;
import com.sprelf.taptimer.Models.Configurable;
import com.sprelf.taptimer.Models.Prefab;
import com.sprelf.taptimer.R;
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
public class PropertyConfigActivity extends Activity
{
    // Field names for items in intent extras
    public static final String PREFAB_EXTRA = "PrefabExtra";
    public static final String ACTIVEITEM_EXTRA = "ActiveItemExtra";
    public static final String DELETE_PREFAB_EXTRA = "DeletePrefab";

    private Prefab prefab = null;
    private ActiveItem activeItem = null;
    private View configView;
    private EmojiPickerView emojiPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Check that relevant data was passed to this activity.  If not, quit immediately
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null ||
            (!intent.getExtras().containsKey(PREFAB_EXTRA) &&
             !intent.getExtras().containsKey(ACTIVEITEM_EXTRA)))
        {
            finish();
        }
        else
        {
            Bundle extras = intent.getExtras();

            // If this activity was started to modify a prefab
            if (extras.containsKey(PREFAB_EXTRA))
            {
                // Get the prefab object to modify
                prefab = extras.getParcelable(PREFAB_EXTRA);

                // If extraction failed, cancel out of activity
                if (prefab == null)
                    finish();
                else
                    initializeConfigurable(prefab);

            }
            // If this activity was started to modify an active item
            else if (extras.containsKey(ACTIVEITEM_EXTRA))
            {
                // Get the active item object to modify
                activeItem = extras.getParcelable(ACTIVEITEM_EXTRA);

                // If extraction failed, cancel out of activity
                if (activeItem == null)
                    finish();
                else
                    initializeConfigurable(activeItem);
            }
            else
                finish();
        }


    }

    /** Performs all actions necessary to initialize this activity with the given Configurable.
     *
     * @param configurable Configurable object to initialize activity with.
     */
    private void initializeConfigurable(Configurable configurable)
    {
        // Get the configuration window layout from the prefab
        configView = configurable.getConfigView(this);

        // Have the prefab identify its EmojiPickerView, if it has any
        emojiPickerView = configurable.identifyEmojiPickerView(configView);
        // If the Emoji Picker exists, mark this activity as its host activity
        // for launching the EmojiPickerActivity from
        if (emojiPickerView != null)
            emojiPickerView.setActivity(this);

        // Apply the default prefab config layout to this activity
        setContentView(R.layout.dialog_prefab_config);

        // Add the prefab's custom config layout to this activity's layout
        ((ViewGroup)findViewById(R.id.PrefabConfig_CustomPrefabArea))
                .addView(configView);
    }

    /**
     * @inheritDoc For PropertyConfigActivity, handles the result of an EmojiPickerActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case EmojiPickerView.RESULT_EMOJI_PICKED:
                    // If this activity has an emoji picker view, and the returned intent contains
                    // valid data, set the emoji picker view to the selected emoji
                    if (emojiPickerView != null &&
                        data != null &&
                        data.getExtras() != null &&
                        data.getExtras().containsKey(EmojiPickerView.EMOJI_EXTRA))
                    {
                        emojiPickerView.setIcon(data.getExtras().getString(
                                EmojiPickerView.EMOJI_EXTRA));
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onConfirmClick(View view)
    {
        closeWithSuccess();
    }

    public void onCancelClick(View view)
    {
        closeWithFailure();
    }

    public void onDeleteClick(View view)
    {
        closeWithDeletion();
    }


    /**
     * Closes this activity, reporting success.  If this activity was called as a response to
     * placing a widget, this indicates that the widget may go ahead and be placed.
     */
    private void closeWithSuccess()
    {
        Intent result = new Intent();

        // If editing a prefab, attach the prefab data to the intent
        if (prefab != null)
        {
            prefab.absorbConfigViewValues(configView);
            result.putExtra(PREFAB_EXTRA, prefab);
        }
        // If editing an active item, attach the active item data to the intent
        else if (activeItem != null)
        {
            activeItem.absorbConfigViewValues(configView);
            result.putExtra(ACTIVEITEM_EXTRA, activeItem);
        }
        // Set a success result code and pass the data back
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
     * Closes the activity, reporting success, and that the selected prefab should be deleted
     * rather than modified.
     */
    private void closeWithDeletion()
    {
        // If there is no prefab, close with failure instead
        if (prefab == null)
        {
            closeWithFailure();
            return;
        }

        // Build the return intent and include a marker that the prefab should be deleted
        Intent result = new Intent();
        result.putExtra(DELETE_PREFAB_EXTRA, true);

        // Set a success result code and pass the data back
        setResult(RESULT_OK, result);
        finish();
    }
}
