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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.codernauti.spaceroverexpress.tutorial.TutorialDialogFragment;
import com.codernauti.spaceroverexpress.tutorial.TutorialMessages;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    public static final String POI_KEY = "poi_key";

    private ViewGroup mHitLayout;
    private MiniGameAdapter mMiniGameAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mGameOneStatus;
    private ImageView mGameTwoStatus;
    private TextView mTimeCounter;

    private Handler mHandler = new Handler();
    private Timer mTimer;

    private int mTotalAttempts;
    private boolean[] mGamesStatuses = new boolean[2];

    private long mBeginTime = 0;
    private long mMinsPassed;
    private long mSecsPassed;

    // DEBUG
    private int mPressTimer;
    private static final int mDebugSeed = 42;

    Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_act);

        // get poi info
        getIntent().getExtras();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // TODO DEBUG
        //mMiniGameAdapter = new MiniGameAdapter(getSupportFragmentManager(), mDebugSeed);
        mMiniGameAdapter = new MiniGameAdapter(getSupportFragmentManager(), new Random().nextInt(100), this);


        mHitLayout = findViewById(R.id.game_act_layout);

        mGameOneStatus = findViewById(R.id.mini_game_one_status);
        mGameTwoStatus = findViewById(R.id.mini_game_two_status);
        mTimeCounter = findViewById(R.id.game_time_counter);

        // TODO: DEBUG
        mTimeCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPressTimer++;
                if (mPressTimer > 5) {
                    mGamesStatuses[0] = true;
                    mGamesStatuses[1] = true;
                    checkGameCompleted();
                }
            }
        });

        mViewPager = findViewById(R.id.game_view_pager);
        mViewPager.setAdapter(mMiniGameAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout = findViewById(R.id.game_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if (SharedPrefUtils.getBooleanPreference(this, PrefKey.PILOT_ASSISTANT_MODE)) {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        mViewPager.setCurrentItem(MiniGameAdapter.getStartFragment(this));

        //mBeginTime = SystemClock.uptimeMillis();

        mTimer = new Timer();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!SharedPrefUtils.getBooleanPreference(this, PrefKey.FIRST_OPEN_GAME)){
            TutorialDialogFragment dialogTutorial =
                    new TutorialDialogFragment(this,
                            PrefKey.FIRST_OPEN_GAME,
                            TutorialMessages.TutorialGame,
                            false);
            dialogTutorial.setCancelable(false);
            dialogTutorial.setCanceledOnTouchOutside(false);
            dialogTutorial.show();
        }

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBeginTime += 1000;

                        long totSec = mBeginTime / 1000;
                        mMinsPassed = totSec / 60;
                        mSecsPassed = totSec % 60;
                        String timeCounter = String.format(Locale.ENGLISH, "%02d:%02d", mMinsPassed, mSecsPassed);
                        mTimeCounter.setText(timeCounter);
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    protected void onStop() {
        mTimer.cancel();
        super.onStop();
    }

    // For GameFragment
    public void onGameFail(int game) {
        /*if (mTotalAttempts == 0) {
            mHitLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.game_hurt_background));
        } else if (mTotalAttempts == 1) {
            mHitLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.game_hurt_background_lvl_2));
        } else if (mTotalAttempts == 2) {
            mHitLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.game_hurt_background_lvl_3));
        }*/

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(500); // for 500 ms
        }

        mTotalAttempts++;
    }


    // TODO: use a listener
    public void onGameCompleted(int game) {
        mGamesStatuses[game] = true;

        switch (game) {
            case 0: {
                mGameOneStatus.setImageDrawable(getDrawable(R.drawable.game_light_on));
                break;
            }
            case 1: {
                mGameTwoStatus.setImageDrawable(getDrawable(R.drawable.game_light_on));
                break;
            }
        }

        checkGameCompleted();
    }

    private void checkGameCompleted() {
        if (mGamesStatuses[0] && mGamesStatuses[1]) {
            Intent intent = GameEndActivity.getStartIntent(this,
                    mMinsPassed, mSecsPassed, mTotalAttempts);

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
