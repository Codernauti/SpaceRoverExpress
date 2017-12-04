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

package com.codernauti.spaceroverexpress.game.manual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.codernauti.spaceroverexpress.R;

public class ManualActivity extends AppCompatActivity {

    private static final String TAG = "ManualActivity";

    public static final String SEED_KEY = "seed_key";
    public static final String DIFFICULTY = "difficulty";

    private ViewPager mViewPager;
    private ManualPagerAdapter mAdapter;
    private TabLayout mTabLayout;

    private int mSeed;
    private int mDifficulty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_act);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }

        if (savedInstanceState != null) {
            mSeed = savedInstanceState.getInt(SEED_KEY);
            mSeed = getIntent().getIntExtra(SEED_KEY, 42);
            mDifficulty = getIntent().getIntExtra(DIFFICULTY, 4);
        }

        Log.d(TAG, "Seed : " + String.valueOf(mSeed));
        Log.d(TAG, "Difficulty : " + String.valueOf(mDifficulty));

        mAdapter = new ManualPagerAdapter(getSupportFragmentManager(), mSeed, this);

        mViewPager = findViewById(R.id.manual_view_pager);
        mViewPager.setAdapter(mAdapter);

        mTabLayout = findViewById(R.id.manual_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }
}
