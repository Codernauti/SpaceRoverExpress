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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codernauti.spaceroverexpress.DashboardActivity;
import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.codernauti.spaceroverexpress.tutorial.TutorialDialogFragment;
import com.codernauti.spaceroverexpress.tutorial.TutorialMessages;

import java.util.ArrayList;

public class GameStartActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "GameStartActivity";

    private boolean mIsPilotSelected = true;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private GameModeAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_start_act);

        mAdapter = new GameModeAdapter(getSupportFragmentManager());

        mToolbar = findViewById(R.id.mode_toolbar);

        findViewById(R.id.mode_go_fab).setOnClickListener(this);

        mViewPager = findViewById(R.id.mode_view_pager);
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(this);

        //new TutorialDialog(this).show();

        if(!SharedPrefUtils.getBooleanPreference(this, PrefKey.FIRST_OPEN_ROLE)){
            TutorialDialogFragment dialogTutorial =
                    new TutorialDialogFragment(this,
                                                       PrefKey.FIRST_OPEN_ROLE,
                                                       TutorialMessages.TutorialChooseRole,
                                                       false );
            dialogTutorial.setCancelable(false);
            dialogTutorial.setCanceledOnTouchOutside(false);
            dialogTutorial.show();
        }
    }

    // Buttons callbacks

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mode_go_fab:
                if (mIsPilotSelected) {
                    startActivity(new Intent(this, DashboardActivity.class));
                } else {
                    startActivity(new Intent(this, ManualStartActivity.class));
                }
                break;
        }
    }

    // ViewPager callbacks

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mIsPilotSelected = !mIsPilotSelected;
        Log.d(TAG, "Pilot Mode: " + mIsPilotSelected);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    // For ModeFragment
    public void changeRole() {
        if (mIsPilotSelected) {
            mViewPager.setCurrentItem(1);
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    private class GameModeAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragments = new ArrayList<>();

        GameModeAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

            mFragments.add(ModeFragment.newInstance(true));
            mFragments.add(ModeFragment.newInstance(false));
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
