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

package com.codernauti.spaceroverexpress.game.colorButtons;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.GameActivity;
import com.codernauti.spaceroverexpress.game.GameFragment;
import com.codernauti.spaceroverexpress.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class ColorButtonsFragment extends GameFragment implements View.OnClickListener {

    private static final String TAG = "ColorButtonsFragment";

    private static final int TITLE = R.string.color_buttons_title;
    private static final String GAME_DONE = "game_done";

    private ConstraintLayout mPageFragment;
    private Animation shakeAnimation;

    private Button mSecondBtn;
    private Button mFirstBtn;
    private Button mThirdBtn;
    private Button mFourthBtn;
    private Button mDoneBtn;

    private ColorButtonsCreator mGameCreator;

    private List<ColorButton> mColorButtons;
    private ArrayList<ColorButton> mPlayerSeqButtonsClicked = new ArrayList<>();

    public static ColorButtonsFragment newInstance(int seed) {
        ColorButtonsFragment fragment = new ColorButtonsFragment();
        GameFragment.setGeneralArgs(fragment, seed);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameCreator = new ColorButtonsCreator(super.getSeed(), LocaleHelper.getLanguage(getContext()).equals(LocaleHelper.LOCALE_IT));
        mColorButtons = mGameCreator.createColorButtons();

        for (int i = 0; i < mColorButtons.size(); i++) {
            Log.d(TAG, mColorButtons.get(i).getText() + " - " + mColorButtons.get(i).getColorText());
        }

        shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.color_buttons_frag, container, false);

        mPageFragment = (ConstraintLayout) root.findViewById(R.id.color_buttons_layout);

        mFirstBtn = root.findViewById(R.id.first_button);
        mSecondBtn = root.findViewById(R.id.second_button);
        mThirdBtn = root.findViewById(R.id.third_button);
        mFourthBtn = root.findViewById(R.id.fourth_button);
        mDoneBtn = root.findViewById(R.id.done_button);

        setColorButton(mFirstBtn, 0);
        setColorButton(mSecondBtn, 1);
        setColorButton(mThirdBtn, 2);
        setColorButton(mFourthBtn, 3);

        mDoneBtn.setOnClickListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (super.isGameCompleted()) {
            mFirstBtn.setEnabled(false);
            mSecondBtn.setEnabled(false);
            mThirdBtn.setEnabled(false);
            mFourthBtn.setEnabled(false);
            mDoneBtn.setEnabled(false);
        }
    }

    void setColorButton(Button button, int index) {
        button.setOnClickListener(this);

        button.setText(mColorButtons.get(index).getText());
        Log.d("ColorButtons", mColorButtons.get(index).getColorText());
        button.setTextColor(Color.parseColor(mColorButtons.get(index).getColorText()));
    }

    @Override
    public int getGameTitle() {
        return TITLE;
    }

    // Buttons callbacks

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.first_button: {
                mFirstBtn.setEnabled(false);
                mPlayerSeqButtonsClicked.add(mColorButtons.get(0));
                break;
            }
            case R.id.second_button: {
                mSecondBtn.setEnabled(false);
                mPlayerSeqButtonsClicked.add(mColorButtons.get(1));
                break;
            }
            case R.id.third_button: {
                mThirdBtn.setEnabled(false);
                mPlayerSeqButtonsClicked.add(mColorButtons.get(2));
                break;
            }
            case R.id.fourth_button: {
                mFourthBtn.setEnabled(false);
                mPlayerSeqButtonsClicked.add(mColorButtons.get(3));
                break;
            }
            case R.id.done_button: {
                if (allButtonsDisable()) {
                    if (mGameCreator.checkPlayerSequence(mPlayerSeqButtonsClicked)) {
                        // TODO: use listener
                        super.onGameCompleted(1);
                        mDoneBtn.setEnabled(false);
                    } else {
                        resetButtonClickedSequence();
                        ((GameActivity) getActivity()).onGameFail(1);
                        mPageFragment.startAnimation(shakeAnimation);
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_color_button), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private boolean allButtonsDisable() {
        return !mFirstBtn.isEnabled()
                && !mSecondBtn.isEnabled()
                && !mThirdBtn.isEnabled()
                && !mFourthBtn.isEnabled();
    }

    private void resetButtonClickedSequence() {
        mFirstBtn.setEnabled(true);
        mSecondBtn.setEnabled(true);
        mThirdBtn.setEnabled(true);
        mFourthBtn.setEnabled(true);

        mPlayerSeqButtonsClicked.clear();
    }
}
