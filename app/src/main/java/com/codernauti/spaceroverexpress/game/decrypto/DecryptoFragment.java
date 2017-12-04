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

package com.codernauti.spaceroverexpress.game.decrypto;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.GameActivity;
import com.codernauti.spaceroverexpress.game.GameFragment;

import java.util.ArrayList;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.view.Gravity.CENTER;

public class DecryptoFragment extends GameFragment implements View.OnClickListener{

    private static final String TAG = "DecryptoFragment";
    private static final int TITLE = R.string.decrypto_title;

    private static final String CORRECT_SEQUENCE_KEY = "correct_sequence_key";

    private static final int NUM_CHAR_TO_DECRYPT = 4;
    private static final String UP = "UP_";
    private static final String DOWN = "DOWN_";
    private static final String NUMB = "NUMB_";
    private static final String CRYP = "CRYP_";

    private DecryptoCreator mDecryptoCreator;
    private ArrayList<CryptedChar> mCryptedChars;

    // TODO: remove this field use mPlayerSequence instead
    private ArrayList<Integer>  mCombinationSelectedList = new ArrayList<>();
    private int[] mPlayerSequence = new int[NUM_CHAR_TO_DECRYPT];

    private LinearLayout mUpArrowsBoard;
    private LinearLayout mBottomArrowsBoard;
    private LinearLayout mCombinationBoard;
    private LinearLayout mCryptoStringBoard;

    private ImageButton[] mTopArrowsButton;
    private ImageButton[] mBottomArrowsButton;
    private TextView[] mCombinationNumberTextView;
    private TextView[] mCryptoStringTextView;

    private LinearLayout mPageFragment;
    private Animation shakeAnimation;

    private Button mUnlockButton;

    public static DecryptoFragment newInstance(int seed) {
        DecryptoFragment fragment = new DecryptoFragment();
        GameFragment.setGeneralArgs(fragment, seed);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDecryptoCreator = new DecryptoCreator(super.getSeed(), NUM_CHAR_TO_DECRYPT);
        mCryptedChars = mDecryptoCreator.createCryptedChars();

        shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.decrypto_frag, container, false);

        mPageFragment = (LinearLayout) root.findViewById(R.id.decrypto_layout);

        mUpArrowsBoard = root.findViewById(R.id.up_arrows_view);
        mUpArrowsBoard.setWeightSum(NUM_CHAR_TO_DECRYPT);
        mBottomArrowsBoard = root.findViewById(R.id.down_arrows_view);
        mBottomArrowsBoard.setWeightSum(NUM_CHAR_TO_DECRYPT);
        mCombinationBoard = root.findViewById(R.id.combination_number_view);
        mCombinationBoard.setWeightSum(NUM_CHAR_TO_DECRYPT);
        mCryptoStringBoard = root.findViewById(R.id.crypto_string_view);
        mCryptoStringBoard.setWeightSum(NUM_CHAR_TO_DECRYPT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;

        mTopArrowsButton = new ImageButton[NUM_CHAR_TO_DECRYPT];
        mBottomArrowsButton = new ImageButton[NUM_CHAR_TO_DECRYPT];
        mCombinationNumberTextView = new TextView[NUM_CHAR_TO_DECRYPT];
        mCryptoStringTextView = new TextView[NUM_CHAR_TO_DECRYPT];

        mUnlockButton = root.findViewById(R.id.unlock_button);
        mUnlockButton.setOnClickListener(this);

        for(int i = 0; i < NUM_CHAR_TO_DECRYPT; i++){
            mTopArrowsButton[i] = new ImageButton(getContext());
            mTopArrowsButton[i].setLayoutParams(params);
            mTopArrowsButton[i].setBackground(ContextCompat.getDrawable(getContext(), R.drawable.up_button_selector));

            mTopArrowsButton[i].setTag(UP + i);
            mTopArrowsButton[i].setClickable(true);
            mTopArrowsButton[i].setOnClickListener(this);
            mUpArrowsBoard.addView(mTopArrowsButton[i]);
        }

        for(int i = 0; i < NUM_CHAR_TO_DECRYPT; i++){
            mBottomArrowsButton[i] = new ImageButton(getContext());
            mBottomArrowsButton[i].setLayoutParams(params);
            mBottomArrowsButton[i].setBackground(ContextCompat.getDrawable(getContext(), R.drawable.down_button_selector));

            mBottomArrowsButton[i].setTag(DOWN + i);
            mBottomArrowsButton[i].setClickable(true);
            mBottomArrowsButton[i].setOnClickListener(this);
            mBottomArrowsBoard.addView(mBottomArrowsButton[i]);
        }

        for(int i = 0; i < NUM_CHAR_TO_DECRYPT; i++){
            mCombinationNumberTextView[i] = new TextView(getContext());
            mCombinationNumberTextView[i].setLayoutParams(params);
            mCombinationNumberTextView[i].setTextSize(55);
            mCombinationNumberTextView[i].setGravity(CENTER);
            mCombinationNumberTextView[i].setTag(NUMB + i);
            mCombinationBoard.addView(mCombinationNumberTextView[i]);
        }

        for(int i = 0; i < NUM_CHAR_TO_DECRYPT; i++){
            mCryptoStringTextView[i] = new TextView(getContext());
            mCryptoStringTextView[i].setLayoutParams(params);
            mCryptoStringTextView[i].setTextSize(55);
            mCryptoStringTextView[i].setGravity(CENTER);
            mCryptoStringTextView[i].setTag(CRYP + i);
            mCryptoStringBoard.addView(mCryptoStringTextView[i]);
        }

        fillCryptoStringBoard();

        return root;
    }

    private void fillCryptoStringBoard(){
        for(int i = 0; i < NUM_CHAR_TO_DECRYPT; i++){
            mCryptoStringTextView[i].setText(mCryptedChars.get(i).getChar());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (super.isGameCompleted()) {
            gameFinished();
        }

        // restore previous state TODO: this works only when user has win the mini game
        for (int i = 0; i < NUM_CHAR_TO_DECRYPT; i++) {
            mCombinationNumberTextView[i].setText(String.valueOf(mPlayerSequence[i]));
        }
    }

    private void gameFinished() {
        for (int i = 0; i < NUM_CHAR_TO_DECRYPT; i++) {
            mBottomArrowsButton[i].setEnabled(false);
            mTopArrowsButton[i].setEnabled(false);
        }
        mUnlockButton.setBackgroundColor(GREEN);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();

        if(tag != null && tag.contains(UP)){
            String idStr =  tag.substring(tag.length() - 1);
            int id = Integer.valueOf(idStr);
            int n = Integer.parseInt((String) mCombinationNumberTextView[id].getText());
            if(n == 9){
                n = 0;
            }else{
                n++;
            }
            mCombinationNumberTextView[id].setText(String.valueOf(n));
        }
        else if(tag != null && tag.contains(DOWN)) {
            String idStr =  tag.substring(tag.length() - 1);
            int id = Integer.valueOf(idStr);
            int n = Integer.parseInt((String) mCombinationNumberTextView[id].getText());
            if(n == 0){
                n = 9;
            }else{
                n--;
            }
            mCombinationNumberTextView[id].setText(String.valueOf(n));
        }
        else if(v.getId() == R.id.unlock_button) {

            mCombinationSelectedList.clear();

            for(int i = 0; i < NUM_CHAR_TO_DECRYPT; i++) {
                mCombinationSelectedList.add(i, Integer.parseInt(mCombinationNumberTextView[i].getText().toString()));
                mPlayerSequence[0] = Integer.parseInt(mCombinationNumberTextView[i].getText().toString());
            }

            if(mDecryptoCreator.checkCombination(mCombinationSelectedList, mCryptedChars)) {
                super.onGameCompleted(0);
                gameFinished();
            } else {
                ((GameActivity) getActivity()).onGameFail(0);
                mPageFragment.startAnimation(shakeAnimation);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(CORRECT_SEQUENCE_KEY, mPlayerSequence);
    }

    @Override
    public int getGameTitle() {
        return TITLE;
    }
}
