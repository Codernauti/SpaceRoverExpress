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

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.DashboardActivity;
import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.POIsCreator;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;

import java.util.Locale;

public class GameEndActivity extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {

    private static final String TAG = "GameEndActivity";

    private static final String MINS_PASSED_KEY = "mins_passed_key";
    private static final String SECS_PASSED_KEY = "secs_passed_key";
    private static final String TOT_ATTEMPTS_KEY = "tot_attempts_key";

    private static final int MAX_STARS = 5;

    private static final int FIVE_STAR_SCORE = 5000;
    private static final int FOUR_STAR_SCORE = 4000;
    private static final int THREE_STAR_SCORE = 3000;
    private static final int TWO_STAR_SCORE = 2000;
    private static final int ONE_STAR_SCORE = 1000;

    private TextView mTimeCounter;
    private TextView mAttemptsCounter;
    private LinearLayout mScoreLayout;
    private TextView mScore;
    private TextView mTotalScore;
    private ImageView mMapIcon;

    private long mFinalScore;

    private Animation shakeAnimation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_end_act);

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        shakeAnimation.setAnimationListener(this);

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }

        if (savedInstanceState != null) {
            mTimeCounter = findViewById(R.id.game_end_time);
            mAttemptsCounter = findViewById(R.id.game_end_attempts);
            mScoreLayout = findViewById(R.id.game_end_stars);
            mScore = findViewById(R.id.game_end_score);
            mTotalScore = findViewById(R.id.game_end_total_score);
            mMapIcon = findViewById(R.id.game_end_map_icon);

            long minsPassed = savedInstanceState.getLong(MINS_PASSED_KEY);
            long secsPassed = savedInstanceState.getLong(SECS_PASSED_KEY);
            String timePassed = String.format(Locale.ENGLISH, "%02d:%02d", minsPassed, secsPassed);
            mTimeCounter.setText(timePassed);

            int attempts = savedInstanceState.getInt(TOT_ATTEMPTS_KEY);
            if(attempts >= 10){
                SharedPrefUtils.saveBooleanPreference(this, PrefKey.TERRAPIATTISTA_BADGE, true);
            }
            mAttemptsCounter.setText(String.valueOf(attempts));

            // TODO: Increase stats into SharedPreferences

            mFinalScore = computeScore(attempts, secsPassed, minsPassed);

            updateUserScore();

            drawScore();

            int idPoi = SharedPrefUtils.getLastDiscoveredPoiIndex(this);
            PointOfInterest poi = POIsCreator.getLocations()[idPoi];

            // update variable in database
            String key = String.valueOf(poi.getId());
            SharedPrefUtils.saveBooleanPreference(this, key, true);

        } else {
            Log.e(TAG, "Cannot get info of finished game");
        }

        mMapIcon.setOnClickListener(this);
        mScoreLayout.setOnClickListener(this);


    }

    private void updateUserScore() {
        // update into sharedPref total score
        long currentScore = SharedPrefUtils.getLongPreference(this, PrefKey.TOTAL_SCORE);
        SharedPrefUtils.saveLongPreference(this, PrefKey.TOTAL_SCORE, currentScore + mFinalScore);

        mScore.setText(String.valueOf(mFinalScore));
        mTotalScore.setText(String.valueOf(currentScore + mFinalScore));
    }

    private int computeScore(int attempts, long secsPassed, long minsPassed) {
        double totalSecs = secsPassed + minsPassed * 60;
        double maxScore = 5000d;
        double maxTime = 180d;

        // TODO: add "attempts" to function
        double gameScore = maxScore * (1 - (totalSecs * totalSecs) / (maxTime * maxTime));

        if (gameScore < 0) {
            gameScore = 0;
        }

        return (int) gameScore;
    }

    private void drawScore() {
        if (mFinalScore >= FIVE_STAR_SCORE) {
            drawStars(5);
        } else if (mFinalScore >= FOUR_STAR_SCORE) {
            drawStars(4);
        } else if (mFinalScore >= THREE_STAR_SCORE) {
            drawStars(3);
        } else if (mFinalScore >= TWO_STAR_SCORE) {
            drawStars(2);
        } else if (mFinalScore >= ONE_STAR_SCORE) {
            drawStars(1);
        } else {
            drawStars(0);
        }

        //shakeAnimation.setRepeatCount(Animation.INFINITE);
        mMapIcon.startAnimation(shakeAnimation);
    }

    private void drawStars(int totalGoldStars) {
        int totalGrayStars = MAX_STARS - totalGoldStars;
        int delay = 200;

        while (totalGoldStars > 0) {
            ImageView starView = (ImageView) getLayoutInflater().inflate(R.layout.game_end_score_item, null);
            starView.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));

            mScoreLayout.addView(starView);

            setAnimationFor(starView, delay);

            delay += 110;
            totalGoldStars--;
        }

        while (totalGrayStars > 0) {
            ImageView starView = (ImageView) getLayoutInflater().inflate(R.layout.game_end_score_item, null);
            starView.setImageDrawable(getDrawable(R.drawable.ic_star_gray_24dp));

            mScoreLayout.addView(starView);

            totalGrayStars--;
        }
    }

    private void setAnimationFor(ImageView view, int delay) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1.8f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1.8f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY);
        animator.setDuration(180);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(1);
        animator.setStartDelay(delay);
        animator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_end_map_icon: {
                SharedPrefUtils.saveBooleanPreference(this, PrefKey.LAST_DISCOVERED_ANIMATE, true);
                SharedPrefUtils.incrementRoverLevel(this);
                Intent intent = DashboardActivity.getStartIntentForStats(this);
                startActivity(intent);
                break;
            }
            case R.id.game_end_stars: {
                mScoreLayout.removeAllViews();
                drawScore();
                break;
            }
        }
    }

    public static Intent getStartIntent(Context context, long minsPassed, long secsPassed, int totalAttempts) {
        Intent intent = new Intent(context, GameEndActivity.class);

        intent.putExtra(MINS_PASSED_KEY, minsPassed);
        intent.putExtra(SECS_PASSED_KEY, secsPassed);
        intent.putExtra(TOT_ATTEMPTS_KEY, totalAttempts);

        return intent;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    private final Handler mHandler = new Handler();

    @Override
    public void onAnimationEnd(Animation animation) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMapIcon.startAnimation(shakeAnimation);
            }
        }, 900);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
