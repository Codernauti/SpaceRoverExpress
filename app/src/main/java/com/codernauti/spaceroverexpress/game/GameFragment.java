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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.codernauti.spaceroverexpress.R;


public class GameFragment extends Fragment {

    private static final String SUPER_TAG = "GameFragment";

    protected static final String SEED_KEY = "seed_key";
    private static final String GAME_STATUS = "game_status";

    private int mSeed;
    private boolean mGameCompleted;

    public static void setGeneralArgs(GameFragment fragment, int seed) {
        Bundle bundle = new Bundle();
        bundle.putInt(SEED_KEY, seed);
        fragment.setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }

        if (savedInstanceState != null) {
            mSeed = savedInstanceState.getInt(SEED_KEY);
            Log.d(SUPER_TAG, "Seed set: " + mSeed);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEED_KEY, mSeed);
        outState.putBoolean(GAME_STATUS, mGameCompleted);
    }

    // For subclasses purpose

    public int getGameTitle() {
        return R.string.default_game_title;
    }

    public int getSeed() {
        return mSeed;
    }

    public boolean isGameCompleted() {
        return mGameCompleted;
    }

    public void onGameCompleted(int gamePosition) {
        mGameCompleted = true;
        ((GameActivity) getActivity()).onGameCompleted(gamePosition);
    }
}
