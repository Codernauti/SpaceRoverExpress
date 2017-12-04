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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.manual.ManualActivity;
import com.codernauti.spaceroverexpress.game.manual.ManualPageFragment;

import java.util.ArrayList;
import java.util.Collections;

import static android.view.Gravity.CENTER;

public class DecryptoManualPage extends ManualPageFragment {

    private static final String TAG = "DecryptoManualPage";
    private static final int TITLE =  R.string.decrypto_title;

    private int mSeed;
    private int mDifficulty;
    private DecryptoCreator mGameCreator;
    private ArrayList<CryptedChar> mCryptedChars;

    private GridLayout mCharsGridView;
    private TextView[] mCryptoStringTextView;

    public static DecryptoManualPage newInstance(int seed) {
        DecryptoManualPage fragment = new DecryptoManualPage();

        Bundle args = new Bundle();
        args.putInt(ManualActivity.SEED_KEY, seed);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }

        mSeed = savedInstanceState.getInt(ManualActivity.SEED_KEY);
        mDifficulty = savedInstanceState.getInt(ManualActivity.DIFFICULTY);

        mGameCreator = new DecryptoCreator(mSeed, mDifficulty);
        mCryptedChars = mGameCreator.createCryptedChars();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        // root of parent fragment layout
        LinearLayout mLayout = root.findViewById(R.id.manual_page_layout);

        inflater.inflate(R.layout.decrypto_manual_page, mLayout, true);

        mCharsGridView = mLayout.findViewById(R.id.grid_chars_view);


        mCryptoStringTextView = new TextView[mGameCreator.getNumSymbols()];

        fillGrid();

        return root;
    }

    private void fillGrid(){

        Collections.shuffle(mCryptedChars);

        mCharsGridView.removeAllViews();

        int mCharsLenght = mCryptedChars.size();
        int numColumns = 2;
        int numRows = mCharsLenght / numColumns;

        mCharsGridView.setColumnCount(numColumns);
        mCharsGridView.setRowCount(numRows + 1);

        for(int i = 0, c = 0, r = 0; i < mCharsLenght; i++, c++) {

            if(c == numColumns) {
                c = 0;
                r++;
            }

            mCryptoStringTextView[i] = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.crypted_char_item, null);
            mCryptoStringTextView[i].setText(mCryptedChars.get(i).getChar() + "  " + mCryptedChars.get(i).getValue());

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.columnSpec = GridLayout.spec(c, 1f);
            param.rowSpec = GridLayout.spec(r, 1f);

            mCryptoStringTextView[i].setLayoutParams (param);

            mCharsGridView.addView(mCryptoStringTextView[i]);
        }

    }

    @Override
    public int getTitle() {
        return TITLE;
    }

    @Override
    public int getDescription() {
        return R.string.manual_decrypto_description;
    }

    @Override
    public int[] getInstructions() {
        return new int[] {
        };
    }
}
