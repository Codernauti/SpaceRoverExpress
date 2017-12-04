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

package com.codernauti.spaceroverexpress.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.manual.ManualActivity;

/**
 * Created by Eduard on 30-Oct-17.
 */

public class ManualStartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SEED_LENGTH = 1;

    private EditText mStatusCodeEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_start_act);

        mStatusCodeEditText = findViewById(R.id.status_code_input);

        findViewById(R.id.accept_status_code).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_status_code: {

                if (mStatusCodeEditText.getText().length() >= SEED_LENGTH) {
                    Intent intent = new Intent(this, ManualActivity.class);
                    int inputSeed = Integer.parseInt(mStatusCodeEditText.getText().toString());
                    intent.putExtra(ManualActivity.SEED_KEY, inputSeed);

                    startActivity(intent);
                }
                break;
            }
        }
    }
}
