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

package com.codernauti.spaceroverexpress.stats;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.LocaleHelper;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.model.POIsCreator;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StatsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "StatsFragment";

    private static final String POI_KEY = "poi_key";
    private static final String STATE_KEY = "state_key";

    // states of StatsFrag
    private static final int STD_STATE = 0;
    private static final int ANIMATE_STATS_STATE = 1;

    private int mState;

    private static final String LOCATION = "LOCATION";
    private static final String BADGE = "BADGE";
    private static final int MAX_LEVEL = 4;

    private boolean mIsAnimationRun = false;

    private static final int[] mRoverLevelImages = { R.mipmap.rover_1,
                                                     R.mipmap.rover_2,
                                                     R.mipmap.rover_3,
                                                     R.mipmap.rover_4};

    private PointOfInterest[] mPointOfInterests;
    private BadgesCreator mBadgesCreator;
    private Badge[] mBadges;

    private int mNumLocationsFound = 0;
    private int mNumBadgesUnlocked = 0;

    private ImageView mRoverImage;

    private TextView mNumLocationsFoundText;
    private TextView mNumBadgesUnlockedText;

    private GridLayout mGridBadges;
    private GridLayout mGridLocations;

    private ImageView[] mLocationImageView;
    private ImageView[] mBadgeImageView;

    private View rootView;
    private TextView mLevelCounter;
    private TextView mTotalExpCounter;
    private TextView mKmCounter;

    private ImageView mDiscoveredView;
    private ImageView mCoverView;
    private AnimatorSet mInAnimator;
    private AnimatorSet mOutAnimator;
    private AnimatorSet mZoomFadeInAnimator;
    private AnimatorSet mZoomFadeOutAnimator;


    public static Fragment newInstance(PointOfInterest poi) {
        Fragment fragment = new StatsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(POI_KEY, poi);

        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }

        if (savedInstanceState != null) {
            mState = getArguments().getInt(STATE_KEY);  // default 0 = STD_STATE
        }

        mBadgesCreator = new BadgesCreator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // root is a ScrollView
        rootView = inflater.inflate(R.layout.stats_frag, container, false);

        mRoverImage = rootView.findViewById(R.id.stats_rover_img);

        mLevelCounter = rootView.findViewById(R.id.stats_rover_level);
        mTotalExpCounter = rootView.findViewById(R.id.stats_rover_exp);
        mKmCounter = rootView.findViewById(R.id.stats_rover_km);

        mNumLocationsFoundText = rootView.findViewById(R.id.location_finded_text);
        mGridLocations = rootView.findViewById(R.id.grid_locations);

        mNumBadgesUnlockedText = rootView.findViewById(R.id.badges_unlocked_text);
        mGridBadges = rootView.findViewById(R.id.grid_badges);

        // init view
        mCoverView = rootView.findViewById(R.id.location_cover_item);
        mDiscoveredView = rootView.findViewById(R.id.location_discovered_item);


        setRoverLevel();

        setUserExpCounter();
        setKilometerCounter();

        buildLocationsGrid();
        buildBadgesGrid();

        return rootView;
    }

    private void setRoverLevel(){
        int levelRover = SharedPrefUtils.getIntPreference(getContext(), PrefKey.ROVER_LEVEL);

        if(levelRover == 0)
        {
            levelRover ++;
        }
        mLevelCounter.setText(getString(R.string.level_default) + " " + levelRover);

        if(levelRover != SharedPrefUtils.DEFAULT_INT_VALUE ){
            if(levelRover > MAX_LEVEL) levelRover = MAX_LEVEL;
            mRoverImage.setImageDrawable(ContextCompat.getDrawable(getContext(), mRoverLevelImages[levelRover - 1]));
        }
    }

    private void setUserExpCounter() {
        long totalScore = SharedPrefUtils.getLongPreference(getContext(), PrefKey.TOTAL_SCORE);
        mTotalExpCounter.setText(String.valueOf(totalScore));
    }

    private void setKilometerCounter() {
        float totalMt = SharedPrefUtils.getFloatPreference(getContext(), PrefKey.TOTAL_METERS_TRAVELED);
        float totalKm = totalMt / 1000;

        DecimalFormatSymbols otherSymbols = null;

        if (LocaleHelper.getLanguage(getContext()).equals(LocaleHelper.LOCALE_IT)) {
            otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
        } else {
            otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
        }

        DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);
        String result = df.format(totalKm);
        mKmCounter.setText(result);
    }

    private void buildLocationsGrid(){

        mPointOfInterests = POIsCreator.getLocations();
        mGridLocations.removeAllViews();

        int locationsLength = mPointOfInterests.length;
        int numColumns = 5;
        int numRows = locationsLength / numColumns;

        mGridLocations.setColumnCount(numColumns);
        mGridLocations.setRowCount(numRows + 1);

        mLocationImageView = new ImageView[locationsLength];

        for(int i = 0, c = 0, r = 0; i < locationsLength; i++, c++)
        {
            if(c == numColumns)
            {
                c = 0;
                r++;
            }

            mLocationImageView[i] = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.location_item, null);
            mLocationImageView[i].setImageResource(mPointOfInterests[i].getPreviewImageResource());
            mLocationImageView[i].setId(mPointOfInterests[i].getId());
            mLocationImageView[i].setTag(LOCATION);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.columnSpec = GridLayout.spec(c, 1f);
            param.rowSpec = GridLayout.spec(r, 1f);

            mLocationImageView[i].setLayoutParams (param);
            mLocationImageView[i].setOnClickListener(this);

            mGridLocations.addView(mLocationImageView[i]);
        }

    }

    private void buildBadgesGrid(){

        mBadges = mBadgesCreator.getBadges();
        mGridBadges.removeAllViews();

        int badgesLength = mBadges.length;
        int numColumns = 5;
        int numRows = badgesLength / numColumns;

        mGridBadges.setColumnCount(numColumns);
        mGridBadges.setRowCount(numRows + 1);

        mBadgeImageView = new ImageView[badgesLength];

        for(int i = 0, c = 0, r = 0; i < badgesLength; i++, c++) {

            if(c == numColumns) {
                c = 0;
                r++;
            }

            mBadgeImageView[i] = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.badge_item, null);
            mBadgeImageView[i].setImageResource(mBadges[i].getImageResource());
            mBadgeImageView[i].setId(mBadges[i].getId());
            mBadgeImageView[i].setTag(BADGE);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.columnSpec = GridLayout.spec(c, 1f);
            param.rowSpec = GridLayout.spec(r, 1f);

            mBadgeImageView[i].setLayoutParams (param);
            mBadgeImageView[i].setClickable(true);
            mBadgeImageView[i].setOnClickListener(this);

            mGridBadges.addView(mBadgeImageView[i]);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /*Bundle args = getArguments();

        if (args != null) {
            unlockNewLocationAnimation();
            SharedPrefUtils.saveBooleanPreference(getContext(), PrefKey.LAST_DISCOVERED_ANIMATE, true);
        }*/

        if (SharedPrefUtils.getBooleanPreference(getContext(), PrefKey.LAST_DISCOVERED_ANIMATE)) {
            unlockNewLocationAnimation();
            SharedPrefUtils.saveBooleanPreference(getContext(), PrefKey.LAST_DISCOVERED_ANIMATE, false);
        }
    }

    private void unlockNewLocationAnimation() {
        int idPoi = SharedPrefUtils.getLastDiscoveredPoiIndex(getContext());
        final PointOfInterest poi = POIsCreator.getLocations()[idPoi];

        mIsAnimationRun = true;

        if (poi != null) {
            // init views
            mCoverView.setVisibility(View.VISIBLE);
            mDiscoveredView.setImageResource(poi.getPreviewImageResource());

            // init animators
            mInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.in_location_discovered);
            mOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.out_location_discovered);
            mZoomFadeInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.zoom_bounce_fade_in);
            mZoomFadeOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.zoom_fade_out);

            // init animations chain
            mZoomFadeInAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDiscoveredView.setVisibility(View.VISIBLE);

                    mOutAnimator.setTarget(mCoverView);
                    mInAnimator.setTarget(mDiscoveredView);

                    mOutAnimator.start();
                    mInAnimator.start();
                }
            });

            mOutAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCoverView.setVisibility(View.GONE);

                    mZoomFadeOutAnimator.setTarget(mDiscoveredView);
                    mZoomFadeOutAnimator.start();
                }
            });

            mZoomFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDiscoveredView.setVisibility(View.GONE);
                    mLocationImageView[poi.getId()].startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                }
            });

            mZoomFadeInAnimator.setTarget(mCoverView);
            mZoomFadeInAnimator.start();
            // Animation chain: zoomFadeIn -> inAnimator & outAnimator -> zoomFadeOut -> shake
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        checkLocationFromShared();
        checkBadgesFromShared();
    }

    public void checkLocationFromShared(){
        mNumLocationsFound = 0;
        for (int i = 0; i < mPointOfInterests.length; i++){
            if (SharedPrefUtils.getBooleanPreference( getContext(), String.valueOf(mPointOfInterests[i].getId()) )) {
                mLocationImageView[i].setClickable(true);
                mLocationImageView[i].setAlpha(1f);
                mNumLocationsFound++;
            } else {
                mLocationImageView[i].setClickable(false);
                mLocationImageView[i].setAlpha(0.2f);
            }
        }
        String numLocationsStr = String.valueOf(mNumLocationsFound);
        if(mNumLocationsFound >= 5 ){
            SharedPrefUtils.saveBooleanPreference(getContext(), PrefKey.TRAVELER_BADGE, true);
        }
        mNumLocationsFoundText.setText(numLocationsStr);
    }

    public void checkBadgesFromShared(){
        mNumBadgesUnlocked = 0;
        for(int i = 0; i < mBadges.length; i++) {
            if (SharedPrefUtils.getBooleanPreference(getContext(), mBadges[i].getKey())) {
                mBadgeImageView[i].setAlpha(1f);
                mNumBadgesUnlocked++;
            } else {
                mBadgeImageView[i].setAlpha(0.2f);
            }
        }

        String numBadgesStr = String.valueOf(mNumBadgesUnlocked);
        mNumBadgesUnlockedText.setText(numBadgesStr);
    }

    @Override
    public void onStop() {
        if(mIsAnimationRun) {
            mInAnimator.cancel();
            mOutAnimator.cancel();
            mZoomFadeInAnimator.cancel();
            mZoomFadeOutAnimator.cancel();
        }

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY, ANIMATE_STATS_STATE);
    }

    // Listeners

    @Override
    public void onClick(View v) {

        String tag = (String) v.getTag();

        if(tag.contains(LOCATION)){
            showLocation(v.getId());
        }
        else if(tag.contains(BADGE)){
            showBadge(v.getId());
        }
    }

    public void showLocation(int id){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = LocationDialogFragment.newInstance(mPointOfInterests[id]);
        newFragment.show(ft, "dialog");
    }

    public void showBadge(int id){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = BadgeDialogFragment.newInstance(mBadges[id]);
        newFragment.show(ft, "dialog");
    }

}