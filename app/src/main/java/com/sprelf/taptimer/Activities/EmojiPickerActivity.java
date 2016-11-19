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

public class EmojiPickerActivity extends FragmentActivity
        implements EmojiconGridFragment.OnEmojiconClickedListener,
                   EmojiconsFragment.OnEmojiconBackspaceClickedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_picker);

        setResult(RESULT_CANCELED);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon)
    {
        Log.d("[EmojiPicker]", "PICKED: " + emojicon.getEmoji());
        Intent intent = new Intent();
        intent.putExtra(EmojiPickerView.EMOJI_EXTRA, emojicon.getEmoji());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onEmojiconBackspaceClicked(View v)
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
