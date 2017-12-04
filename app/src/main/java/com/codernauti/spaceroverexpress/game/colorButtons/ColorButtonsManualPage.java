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
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.manual.ManualActivity;
import com.codernauti.spaceroverexpress.game.manual.ManualPageFragment;
import com.codernauti.spaceroverexpress.utils.LocaleHelper;

import java.util.ArrayList;

/**
 * Created by Eduard on 31-Oct-17.
 */

public class ColorButtonsManualPage extends ManualPageFragment {

    private static final int TITLE = R.string.color_buttons_title;

    private int mSeed;
    private ColorButtonsCreator mGameCreator;

    private LinearLayout mLayout;

    public static ColorButtonsManualPage newInstance(int seed) {
        ColorButtonsManualPage fragment = new ColorButtonsManualPage();

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
        mGameCreator = new ColorButtonsCreator(mSeed, LocaleHelper.getLanguage(getContext()).equals(LocaleHelper.LOCALE_IT));
        mGameCreator.createColorButtons();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // retrieve the super class layout (ScrollView)
        View root = super.onCreateView(inflater, container, savedInstanceState);
        // get the layout where inflate new views
        mLayout = root.findViewById(R.id.manual_page_layout);

        buildViewsForSets();

        return root;
    }

    private void buildViewsForSets() {
        ArrayList<ArrayList<ColorButton>> possibleSequences = mGameCreator.getPossibleSequences();

        for (ArrayList<ColorButton> sequence : possibleSequences) {

            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            int startSpan = 0;

            for (int i = 0; i < sequence.size(); i++) {
                ColorButton colorButton = sequence.get(i);

                String textColorBtn = colorButton.getText();
                String hexColorBtn = colorButton.getColorText();
                int textColBtnLength = textColorBtn.length();

                stringBuilder.append(textColorBtn);

                if (i != sequence.size() - 1) {
                    stringBuilder.append("\n");
                }

                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(hexColorBtn));

                stringBuilder.setSpan(span, startSpan, startSpan + textColBtnLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                startSpan = startSpan + textColBtnLength + 1;
            }

            // get View from xml
            TextView textView = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.color_buttons_view_set, null);
            textView.setText(stringBuilder);

            // add view to main layout
            mLayout.addView(textView);
        }
    }

    @Override
    public int getTitle() {
        return TITLE;
    }

    @Override
    public int getDescription() {
        return R.string.manual_color_buttons_description;
    }

    @Override
    public int[] getInstructions() {
        return new int[] { R.string.manual_color_buttons_explain_1,
                R.string.manual_color_buttons_explain_2,
                R.string.manual_color_buttons_explain_3
        };
    }
}
