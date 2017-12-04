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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codernauti.spaceroverexpress.game.colorButtons.ColorButtonsManualPage;
import com.codernauti.spaceroverexpress.game.decrypto.DecryptoManualPage;

import java.util.ArrayList;

class ManualPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<ManualPageFragment> mPages = new ArrayList<>();

    private Context mContext;

    ManualPagerAdapter(FragmentManager fm, int seed, Context context) {
        super(fm);

        mContext = context;

        mPages.add(DecryptoManualPage.newInstance(seed));
        mPages.add(ColorButtonsManualPage.newInstance(seed));

        // TODO: add other manual page
        // Collections.shuffle(mPages); // add random
    }

    @Override
    public Fragment getItem(int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mPages.get(position).getTitle());
    }
}
