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

package com.codernauti.spaceroverexpress.game.manual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.GameFragment;

public class ManualPageFragment extends GameFragment {

    private TextView mTitleTextView;
    private TextView mDescTextView;
    private TextView mInstructionsTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.manual_page, container, false);

        mTitleTextView = root.findViewById(R.id.manual_page_title);
        mDescTextView = root.findViewById(R.id.manual_page_description);
        mInstructionsTextView = root.findViewById(R.id.manual_page_instructions);

        setupTitle();
        mDescTextView.setText(getResources().getString((getDescription())));
        setupInstruction();

        return root;
    }

    private void setupTitle() {
        int pageTitle = getTitle();
        String titleString = getContext().getString(pageTitle);
        SpannableString content = new SpannableString(titleString);
        content.setSpan(new UnderlineSpan(), 0, titleString.length(), 0);

        mTitleTextView.setText(getTitle());
    }

    private void setupInstruction() {
        int[] steps = getInstructions();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < steps.length; i++) {
            stringBuilder.append("\u2022  ")
                    .append(getResources().getString(steps[i]));

            if (i != steps.length - 1) { // exclude last iteration
                stringBuilder.append("\n");
            }
        }

        mInstructionsTextView.setText(stringBuilder.toString());
    }

    // Template methods

    public int getTitle() {
        return R.string.default_game_title;
    }

    public int getDescription() {
        return 0;
    }

    public int[] getInstructions() {
        return new int[] {  };
    }

    // GameFragment override

    @Override
    public int getGameTitle() {
        return getTitle();
    }
}
