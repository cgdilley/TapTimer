package com.sprelf.taptimer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sprelf.taptimer.Models.ActiveItem;
import com.sprelf.taptimer.Models.Configurable;
import com.sprelf.taptimer.Models.Prefab;
import com.sprelf.taptimer.R;

import androidx.emoji2.emojipicker.EmojiPickerView;

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
    public static final String CONFIG_EXTRA = "ConfigExtra";
    public static final String DELETE_EXTRA = "DeleteExtra";

    private Configurable configItem = null;
    private View configView;

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
            !intent.getExtras().containsKey(CONFIG_EXTRA))
        {
            finish();
        }
        else
        {
            // Get the configurable object to modify
            configItem = intent.getExtras().getParcelable(CONFIG_EXTRA);

            // If extraction failed, cancel out of activity
            if (configItem == null)
                finish();
            else
                initializeConfigurable(configItem);
        }


    }

    /**
     * Performs all actions necessary to initialize this activity with the given Configurable.
     *
     * @param configurable Configurable object to initialize activity with.
     */
    private void initializeConfigurable(Configurable configurable)
    {
        // Get the configuration window layout from the prefab
        configView = configurable.getConfigView(this);

        if (configurable instanceof Prefab)
        {
            // Apply the default config layout to this activity
            setContentView(R.layout.dialog_prefab_config);
            // Add the prefab's custom config layout to this activity's layout
            ((ViewGroup) findViewById(R.id.PrefabConfig_CustomPrefabArea))
                    .addView(configView);
        }
        else if (configurable instanceof ActiveItem)
        {
            // Apply the default config layout to this activity
            setContentView(R.layout.dialog_activeitem_config);
            // Add the active item's custom config layout to this activity's layout
            ((ViewGroup) findViewById(R.id.ActiveItemConfig_CustomActiveItemArea))
                    .addView(configView);
        }


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

    public void onEmojiSelectClick(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_emojipicker, null);
        EmojiPickerView emojiView = popupView.findViewById(R.id.EmojiPicker);

        final PopupWindow popup = new PopupWindow(popupView,
                                                  ViewGroup.LayoutParams.WRAP_CONTENT,
                                                  ViewGroup.LayoutParams.WRAP_CONTENT,
                                                  true);
        popup.showAsDropDown(view);

        emojiView.setOnEmojiPickedListener(item -> {
            TextView button = PropertyConfigActivity.this.findViewById(R.id.PrefabTimer_Config_IconInput);
            button.setText(item.getEmoji());
            popup.dismiss();
        });
    }


    /**
     * Closes this activity, reporting success.  Passes along the configured Configurable object.
     */
    private void closeWithSuccess()
    {
        configItem.absorbConfigViewValues(configView);

        Intent result = new Intent();
        result.putExtra(CONFIG_EXTRA, configItem);

        // Set a success result code and pass the data back
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * Closes this activity, reporting failure.
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
        if (configItem == null)
        {
            closeWithFailure();
            return;
        }

        // Build the return intent and include a marker that the prefab should be deleted
        Intent result = new Intent();
        result.putExtra(DELETE_EXTRA, true);

        // Set a success result code and pass the data back
        setResult(RESULT_OK, result);
        finish();
    }
}
