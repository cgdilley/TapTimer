package com.sprelf.taptimer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Views.EmojiPickerView;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

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
 * EmojiPickerActivity is an activity for holding an EmojiconsFragment and responding to its
 * event listeners, returning the results of any emojicon selections.
 */
public class EmojiPickerActivity extends FragmentActivity
        implements EmojiconGridFragment.OnEmojiconClickedListener,
                   EmojiconsFragment.OnEmojiconBackspaceClickedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_picker);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
    }

    /** Called when an emojicon in the EmojiconsFragment is clicked.  The selected emoji
     * will be packed into an Intent and returned to the calling activity.
     *
     * @param emojicon The clicked Emojicon
     */
    @Override
    public void onEmojiconClicked(Emojicon emojicon)
    {
        Log.d("[EmojiPicker]", "PICKED: " + emojicon.getEmoji());

        // Create the intent and store the string form of the Emojicon
        Intent intent = new Intent();
        intent.putExtra(EmojiPickerView.EMOJI_EXTRA, emojicon.getEmoji());

        // End the activity, reporting success and sending the built intent
        setResult(RESULT_OK, intent);
        finish();
    }

    /** Called when the backspace button is pressed.  This cancels the activity with no result.
     *
     * @param v View that was clicked
     */
    @Override
    public void onEmojiconBackspaceClicked(View v)
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
