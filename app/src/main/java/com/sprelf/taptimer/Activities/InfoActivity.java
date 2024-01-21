package com.sprelf.taptimer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Chris on 21.01.2024.
 */
public class InfoActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.dialog_info);

    }

    public void onCloseClick(View view)
    {
        finish();
    }
}
