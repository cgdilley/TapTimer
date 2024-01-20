/*
 *     TapTimer - A Timer Widget App
 *     Copyright (C) 2016 Dilley, Christopher
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sprelf.taptimer.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.sprelf.taptimer.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
    {

        public static final int REQUEST_CODE_ALERT_RINGTONE = 100;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.preferences, rootKey);


            Preference feedback = (Preference) this.findPreference("Pref_Feedback");
            feedback.setOnPreferenceClickListener((pref) -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:me@sprelf.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "TapTimer Feedback");
                startActivity(Intent.createChooser(intent,
                                                   getString(R.string.Pref_FeedbackChooser)));
                return true;
            });
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference)
        {
            if (preference.getKey().equals("Pref_Ringtone"))
            {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                                RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                                Settings.System.DEFAULT_ALARM_ALERT_URI);

                String existingValue = getRingtonePreferenceValue(getContext());
                if (existingValue != null)
                {
                    if (existingValue.length() == 0)
                    {
                        // Select "Silent"
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    }
                    else
                    {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                        Uri.parse(existingValue));
                    }
                }
                else
                {
                    // No ringtone has been selected, set to the default
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                    Settings.System.DEFAULT_ALARM_ALERT_URI);
                }

                startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
                return true;
            }
            else
            {
                return super.onPreferenceTreeClick(preference);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            if (requestCode == REQUEST_CODE_ALERT_RINGTONE && data != null)
            {
                Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (ringtone != null)
                {
                    setRingtonePreferenceValue(getContext(), ringtone.toString());
                }
                else
                {
                    // "Silent" was selected
                    setRingtonePreferenceValue(getContext(), "");
                }
            }
            else
            {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        protected String getRingtonePreferenceValue(Context c)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

            return prefs.getString(
                    "Pref_Ringtone",
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
        }

        protected void setRingtonePreferenceValue(Context c, String ringtone)
        {
            SharedPreferences.Editor prefs =
                    PreferenceManager.getDefaultSharedPreferences(c).edit();

            prefs.putString("Pref_Ringtone", ringtone);

            prefs.apply();
        }
    }
}