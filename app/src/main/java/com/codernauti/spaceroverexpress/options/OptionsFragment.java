/* 
Space Rover Express
Copyright (C) 2017 Codernauti
Eduard Bicego, Federico Ghirardelli

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.codernauti.spaceroverexpress.options;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.codernauti.spaceroverexpress.CreditsActivity;
import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.SelectLanguageActivity;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;

public class OptionsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "BadgesFragment";

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Preference creditsPref = findPreference("credits_key");
        creditsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getContext().startActivity(new Intent(getContext(), CreditsActivity.class));
                return true;
            }
        });

        final SwitchPreference manualSpPref = (SwitchPreference) findPreference(PrefKey.MANUAL_MAP_STATE);
        manualSpPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean clickEnable = (Boolean) newValue;

                if (clickEnable) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.plase_note_dialog_title)
                            .setMessage(R.string.manual_mode_message_dialog)
                            .setPositiveButton(getString(R.string.go_ahead), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPrefUtils.saveBooleanPreference(getContext(), PrefKey.MANUAL_MAP_STATE, true);
                                    manualSpPref.setChecked(true);
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { }
                            })
                            .create();

                    dialog.show();

                    return false;   // don't change automatically the preference
                } else {
                    return true;    // change automatically the preference
                }
            }
        });

        final SwitchPreference mapCompassSpPref = (SwitchPreference) findPreference(PrefKey.MAP_COMPASS);
        mapCompassSpPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean clickEnable = (Boolean) newValue;

                if (!clickEnable) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.plase_note_dialog_title)
                            .setMessage(R.string.compass_message_dialog)
                            .setPositiveButton(getString(R.string.disable), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPrefUtils.saveBooleanPreference(getContext(), PrefKey.MAP_COMPASS, false);
                                    mapCompassSpPref.setChecked(false);
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { }
                            })
                            .create();

                    dialog.show();

                    return false;   // don't change automatically the preference
                } else {
                    return true;    // change automatically the preference
                }
            }
        });

        Preference clearSpPref = findPreference("clear_sp_key");
        clearSpPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.clear_sp_dialog_title)
                        .setMessage(R.string.clear_sp_dialog_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPrefUtils.clearSharedPreferences(getContext());
                                Intent intent = new Intent(getContext(), SelectLanguageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .create();

                dialog.show();

                return true;
            }
        });
    }
}