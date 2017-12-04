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

package com.codernauti.spaceroverexpress.game.shell;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.GameFragment;

import java.util.Timer;
import java.util.TimerTask;

public class GameShellFragment extends GameFragment implements TextView.OnEditorActionListener {

    public static final int TITLE = R.string.terminal_title;

    private TextView mInputText;
    private TextView mTextView;

    private Handler mHandler;
    private ShellCreator mShellCreator;

    public static GameShellFragment newInstance(int seed) {
        GameShellFragment fragment = new GameShellFragment();
        GameFragment.setGeneralArgs(fragment, seed);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        mShellCreator = new ShellCreator(super.getSeed());
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.game_shell_frag, container, false);

        mInputText = root.findViewById(R.id.input_shell);
        mTextView = root.findViewById(R.id.view_shell);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String inputStr = mInputText.getText().toString();
                        if (inputStr.equals("")) {
                            mInputText.setText(".");
                        } else if (inputStr.equals(".")){
                            mInputText.setText("..");
                        } else if (inputStr.equals("..")) {
                            mInputText.setText("...");
                        } else {
                            mInputText.setText("");
                        }
                    }
                });
            }
        }, 0, 500);
        //mInputText.setOnEditorActionListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        for (final String shellMsg : mShellCreator.getLoadingStates()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateTextView(shellMsg);
                }
            }, mShellCreator.getShellPrintDelay());
        }
    }

    private void updateTextView(String text) {
        if (!text.isEmpty()) {
            String prevText = mTextView.getText().toString();
            String newText = "";

            if (prevText.isEmpty()) {
                // first input
                newText = text;
            } else {
                newText = prevText + "\n" + text;
            }

            mTextView.setText(newText);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getGameTitle() {
        return TITLE;
    }

    // Soft keyboard callbacks

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // your additional processing...
            String inputText = mInputText.getText().toString();
            mInputText.setText("");
            updateTextView(inputText);
            return true;
        }

        return false;
    }

}
