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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.LocaleHelper;

public class ModeFragment extends Fragment implements View.OnClickListener {

    private static final String ROLE_KEY = "role_key";

    private TextView mNameMode;
    private ImageView mLeftArrow;
    private ImageView mRightArrow;

    private boolean mIsPilot;


    public static ModeFragment newInstance(boolean isPilot) {
        ModeFragment fragment = new ModeFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(ROLE_KEY, isPilot);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }

        // pilot mode
        mIsPilot = true;
        if (savedInstanceState != null) {
            mIsPilot = savedInstanceState.getBoolean(ROLE_KEY);
        }

        // MainLayout
        View root = inflater.inflate(R.layout.mode_fragment, container, false);

        mNameMode = root.findViewById(R.id.mode_name);
        mLeftArrow = root.findViewById(R.id.mode_left_arrow);
        mRightArrow = root.findViewById(R.id.mode_right_arrow);

        mLeftArrow.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);

        if (mIsPilot) {
            setupPilotView(root);
        } else {
            setupAssistantView(root);
        }

        startArrowAnimationFor(mRightArrow);
        startArrowAnimationFor(mLeftArrow);

        return root;
    }

    private void startArrowAnimationFor(ImageView imageView) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1.3f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1.3f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhX, pvhY);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }


    private void setupPilotView(View root) {
        // TODO: change image
        //root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundPilotMode));

        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.pilot);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        root.setBackground(bitmapDrawable);*/

        if(LocaleHelper.getLanguage(getContext()).equals(LocaleHelper.LOCALE_IT)) {
            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.pilot_ita_back));
        } else {
            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.pilot_usa_back));
        }
        mLeftArrow.setVisibility(View.GONE);
        //mNameMode.setText("Pilot");
    }

    private void setupAssistantView(View root) {
        // TODO: change image
        //root.setBackgroundColor(Color.LTGRAY);
        if(LocaleHelper.getLanguage(getContext()).equals(LocaleHelper.LOCALE_IT)) {
            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.assistant_ita_back));
        } else {
            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.assistant_usa_back));
        }
        mRightArrow.setVisibility(View.GONE);
        //mNameMode.setText("Assistant");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ROLE_KEY, mIsPilot);
    }

    // Buttons callbacks

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mode_left_arrow:
                ((GameStartActivity) getActivity()).changeRole();
                break;
            case R.id.mode_right_arrow:
                ((GameStartActivity) getActivity()).changeRole();
                break;
            default:
                ((GameStartActivity) getActivity()).changeRole();
                break;
        }
    }

}
