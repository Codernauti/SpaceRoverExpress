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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Eduard on 01-Nov-17.
 */

class ShellCreator {

    private static String[] loadingStates = {
            "-boot \nCheck system status \nSystem ok.",
            //"system.useless/S/Turn on auxiliar engine... \n100%",
            //"memory.hw./M/Loading general properties...",
            "**reboot",
            "checking.system/C/Check vital functions",
            "checking.system/C/Check flatlanders around",
            //"rover.utilities/U/Clean the MastCam... \n100%",
            "rover.utilities/U/Take a selfie",
            /*"Send nudes to NASA",*/
            "system.network/ -start communication",
            //"-------------------------------------------------------------------",
            "system.moving.control/M/Decreasing support angle: \n3 degree \nok.",
            "rover.utilities/U/Count wheels holes",
            //"system.core/S/Shut down useless functions... **complete",
            //"system.core/S/Turn on pilot monitor \nmonitor one: ok. \nmonitor two ok.",
            "system.core/S/Decreasing the radar flow rate",
            "#   100%   #",
            "system.network/N/Start streaming to NASA",
            "system.useless/S/Print some binary number: \n01010010 \n1010010 \n10001001"
            /*"1% \n7% \n21% \n42% \n67% \n79% \n85% \n94% \n100%"*/
    };

    private final int mSeed;
    private final Random mRandomGenerator;


    ShellCreator(int seed) {
        mSeed = seed;
        mRandomGenerator = new Random(seed);
    }

    ArrayList<String> getLoadingStates() {
        ArrayList<String> result = new ArrayList<>(Arrays.asList(loadingStates));
        result.add(String.format(Locale.ENGLISH, "system.core/S/Status code: %d", mSeed));

        Collections.shuffle(result, mRandomGenerator);

        return result;
    }

    int getShellPrintDelay() {
        return mRandomGenerator.nextInt(50) * 100;
    }




}
