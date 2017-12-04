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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codernauti.spaceroverexpress.game.colorButtons.ColorButtonsManualPage;
import com.codernauti.spaceroverexpress.game.decrypto.DecryptoManualPage;
import com.codernauti.spaceroverexpress.game.shell.GameShellFragment;
import com.codernauti.spaceroverexpress.game.colorButtons.ColorButtonsFragment;
import com.codernauti.spaceroverexpress.game.decrypto.DecryptoFragment;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;

import java.util.ArrayList;

public class MiniGameAdapter extends FragmentPagerAdapter {

    private static final int GAME_SHELL = 1;
    private static final int GAME_SHELL_PILOT_ASSISTANT_MODE = 2;

    private Context mContext;

    private final ArrayList<GameFragment> mGameFragments = new ArrayList<>();

    MiniGameAdapter(FragmentManager fragmentManager, int seed, Context context) {
        super(fragmentManager);

        mContext = context;
        // TODO: get random games

        boolean pilotAssistantMode = SharedPrefUtils.getBooleanPreference(mContext, PrefKey.PILOT_ASSISTANT_MODE);

        if (pilotAssistantMode) {
            mGameFragments.add(DecryptoManualPage.newInstance(seed));
        }

        mGameFragments.add(DecryptoFragment.newInstance(seed));
        mGameFragments.add(GameShellFragment.newInstance(seed));
        mGameFragments.add(ColorButtonsFragment.newInstance(seed));

        if (pilotAssistantMode) {
            mGameFragments.add(ColorButtonsManualPage.newInstance(seed));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mGameFragments.get(position);
    }

    @Override
    public int getCount() {
        return mGameFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mGameFragments.get(position).getGameTitle());
    }

    static int getStartFragment(Context context) {
        if (SharedPrefUtils.getBooleanPreference(context, PrefKey.PILOT_ASSISTANT_MODE)) {
            return GAME_SHELL_PILOT_ASSISTANT_MODE;
        } else {
            return GAME_SHELL;
        }
    }
}
