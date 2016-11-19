package com.sprelf.taptimer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.sprelf.taptimer.Models.ActiveItem;
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

            if (extras.containsKey(PREFAB_EXTRA))
            {
                prefab = extras.getParcelable(PREFAB_EXTRA);
                if (prefab == null)
                    finish();
                else
                {
                    configView = prefab.getConfigLayout(this);
                    emojiPickerView = prefab.identifyEmojiPickerView(configView);
                    if (emojiPickerView != null)
                        emojiPickerView.setActivity(this);

                    setContentView(R.layout.dialog_prefab_config);
                    ((ViewGroup)findViewById(R.id.PrefabConfig_CustomPrefabArea))
                            .addView(configView);
                }
            }
            else if (extras.containsKey(ACTIVEITEM_EXTRA))
            {
                activeItem = extras.getParcelable(ACTIVEITEM_EXTRA);
                if (activeItem == null)
                    finish();
                else
                {
                    configView = activeItem.getConfigLayout(this);
                    emojiPickerView = activeItem.identifyEmojiPickerView(configView);
                    if (emojiPickerView != null)
                        emojiPickerView.setActivity(this);

                    setContentView(R.layout.dialog_activeitem_config);
                    ((ViewGroup)findViewById(R.id.ActiveItemConfig_CustomActiveItemArea))
                            .addView(configView);
                }
            }
            else
                finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case EmojiPickerView.RESULT_EMOJI_PICKED:
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

        if (prefab != null)
        {
            prefab.absorbConfigViewValues(configView);
            result.putExtra(PREFAB_EXTRA, prefab);
        }
        else if (activeItem != null)
        {
            activeItem.absorbConfigViewValues(configView);
            result.putExtra(ACTIVEITEM_EXTRA, activeItem);
        }
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

    private void closeWithDeletion()
    {
        if (prefab == null)
        {
            closeWithFailure();
            return;
        }

        Intent result = new Intent();
        result.putExtra(DELETE_PREFAB_EXTRA, true);

        setResult(RESULT_OK, result);
        finish();
    }
}
