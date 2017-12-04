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

package com.codernauti.spaceroverexpress.stats;


import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.PrefKey;

public class BadgesCreator {

    private static final String TAG = "BadgesCreator";

    private Badge[] mBadges = {
            new Badge(0, PrefKey.HALLEY_BADGE, R.string.badge_title_1, R.string.badge_desc_1, R.drawable.cometa),
            new Badge(1, PrefKey.TRAVELER_BADGE, R.string.badge_title_2, R.string.badge_desc_2, R.drawable.traveler),
            new Badge(2, PrefKey.TERRAPIATTISTA_BADGE, R.string.badge_title_3, R.string.badge_desc_3, R.drawable.terrapiattista),
    };

    BadgesCreator() {
    }

    public Badge[] getBadges(){
        return mBadges;
    }

}
