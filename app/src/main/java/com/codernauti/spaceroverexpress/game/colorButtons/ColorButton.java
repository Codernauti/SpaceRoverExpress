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

/**
 * Created by Eduard on 30-Oct-17.
 */

class ColorButton {

    private String mText;
    private String mColorText;   // Hex color code

    ColorButton(String text, String colorText) {
        mText = text;
        mColorText = colorText;
    }

    public String getColorText() {
        return mColorText;
    }

    public String getText() {
        return mText;
    }
}
