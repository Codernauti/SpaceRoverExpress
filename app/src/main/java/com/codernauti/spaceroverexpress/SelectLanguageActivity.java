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

package com.codernauti.spaceroverexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codernauti.spaceroverexpress.game.GameStartActivity;
import com.codernauti.spaceroverexpress.utils.LocaleHelper;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SelectLanguageActivity";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selection_language_act);

        mToolbar = findViewById(R.id.language_toolbar);

        findViewById(R.id.uk_flag_button).setOnClickListener(this);
        findViewById(R.id.ita_flag_button).setOnClickListener(this);

    }

    // Buttons callbacks

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uk_flag_button:
                LocaleHelper.setLocale(getApplicationContext(), LocaleHelper.LOCALE_EN);
                startActivity(new Intent(this, GameStartActivity.class));
                break;
            case R.id.ita_flag_button:
                LocaleHelper.setLocale(getApplicationContext(), LocaleHelper.LOCALE_IT);
                startActivity(new Intent(this, GameStartActivity.class));
                break;
        }
    }
}
