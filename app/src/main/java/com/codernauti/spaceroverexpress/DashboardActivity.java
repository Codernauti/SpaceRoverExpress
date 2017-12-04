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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.codernauti.spaceroverexpress.game.GameActivity;
import com.codernauti.spaceroverexpress.model.POIsCreator;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.navigator.NewNavigatorFragment;
import com.codernauti.spaceroverexpress.options.OptionsFragment;
import com.codernauti.spaceroverexpress.stats.StatsFragment;
import com.codernauti.spaceroverexpress.tutorial.TutorialDialogFragment;
import com.codernauti.spaceroverexpress.tutorial.TutorialMessages;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DashboardActivity";

    private static final String FRAG_OPENED_KEY = "frag_opened_key";

    // BackStack management
    private static final String BACK_STACK_ROOT_TAG = "back_stack_root";

    private int mFragmentToOpen;  // id fragment opened

    private Toolbar mToolbar;
    private BottomNavigationView mNavView;

    private HashMap<Integer, Fragment> mFragments = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_act);

        // needed for the first time user open app, it initialize the preferences on default values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get from save instance state
        if (savedInstanceState != null) {
            mFragmentToOpen = savedInstanceState.getInt(FRAG_OPENED_KEY);
        } else {
            mFragmentToOpen = R.id.dashboard_menu_map;
        }
        Log.d(TAG, "Fragment to opened: " + String.valueOf(mFragmentToOpen));

        mFragmentToOpen = getIntent().getIntExtra(FRAG_OPENED_KEY, mFragmentToOpen);

        Log.d(TAG, String.valueOf(getIntent().getIntExtra(FRAG_OPENED_KEY, 0)));

        mToolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(mToolbar);

        mNavView = findViewById(R.id.dashboard_nav_menu);

        mNavView.setOnNavigationItemSelectedListener(this);
        mNavView.setSelectedItemId(mFragmentToOpen);

        if(!SharedPrefUtils.getBooleanPreference(this, PrefKey.FIRST_OPEN_MAP)) {
            TutorialDialogFragment dialogTutorial =
                    new TutorialDialogFragment(this,
                            PrefKey.FIRST_OPEN_MAP,
                            TutorialMessages.TutorialFirstMap,
                            false);
            dialogTutorial.setCancelable(false);
            dialogTutorial.setCanceledOnTouchOutside(false);
            dialogTutorial.show();
        }
    }

    public void selectFragment(String title) {
        int titleFragId = 0;
        Fragment fragment = null;

        Fragment fragmentFromCache = mFragments.get(mFragmentToOpen);

        switch (mFragmentToOpen) {
            case R.id.dashboard_menu_map: {
                if (fragmentFromCache == null) {
                    mFragments.put(R.id.dashboard_menu_map, new NewNavigatorFragment());
                }
                fragment = mFragments.get(mFragmentToOpen);
                titleFragId = R.id.dashboard_menu_map;

                break;
            }
            case R.id.dashboard_menu_statistics: {
                if (fragmentFromCache == null) {
                    mFragments.put(R.id.dashboard_menu_statistics, new StatsFragment());
                }
                fragment = mFragments.get(mFragmentToOpen);
                titleFragId = R.id.dashboard_menu_statistics;

                break;
            }
            case R.id.dashboard_menu_options: {
                if (fragmentFromCache == null) {
                    mFragments.put(R.id.dashboard_menu_options, new OptionsFragment());
                }
                fragment = mFragments.get(mFragmentToOpen);
                titleFragId = R.id.dashboard_menu_options;

                break;
            }

            // DEBUG
            /*case R.id.dashboard_menu_game:
                startActivity(new Intent(this, GameActivity.class));
                break;*/
        }

        if (fragment != null) {

            Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);

            if (currentFrag != null && currentFrag.getTag().equals(fragment.getTag())) {
                // not add to backStack
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, String.valueOf(titleFragId))
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, String.valueOf(titleFragId))
                        .addToBackStack(String.valueOf(titleFragId))
                        .commit();
            }

            Log.d(TAG, "opened fragment: " + titleFragId);

            setTitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FRAG_OPENED_KEY, mFragmentToOpen);
    }

    public static Intent getStartIntentForStats(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(FRAG_OPENED_KEY, R.id.dashboard_menu_statistics);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();

        if (fragments == 1) {
            finish();
        } else {
            if (fragments > 1) {
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    Log.d(TAG, getSupportFragmentManager().getBackStackEntryAt(i).toString());
                }

                // Current fragment transition is in position "fragments - 1"
                // The previous fragment transition is in position "fragments - 2"
                FragmentManager.BackStackEntry transBeforeCurrent = getSupportFragmentManager().getBackStackEntryAt(fragments - 2);

                // update BottomNavigationView
                mNavView.setOnNavigationItemSelectedListener(null);

                MenuItem item = mNavView.getMenu().findItem(Integer.valueOf(transBeforeCurrent.getName()));
                mNavView.setSelectedItemId(item.getItemId());
                setTitle(item.getTitle());

                mNavView.setOnNavigationItemSelectedListener(this);

                // back to previous fragment
                getSupportFragmentManager().popBackStack();

            } else {
                super.onBackPressed();
            }
        }
    }

    // mNavigatorBottomView callback

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mFragmentToOpen = item.getItemId();
        selectFragment(item.getTitle().toString());
        return true;
    }
}
