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

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

class ColorButtonsCreator {

    private final String[] mBaseStringColorSet;

    private static final String[] mEnBaseStringColorSet = {
            "BLACK",
            "BLUE",
            "CYAN",
            /*"DKGRAY",*/
            /*"GRAY",*/
            "GREEN",
            /*"LTGRAY",*/
            "MAGENTA",
            "RED",
            /*"TRANSPARENT",*/
            "WHITE",
            "ORANGE"
    };

    private static final String[] mItBaseStringColorSet = {
            "NERO",
            "BLU",
            "CIANO",
            "VERDE",
            "MAGENTA",
            "ROSSO",
            "BIANCO",
            "ARANCIO"
    };

    private static final String[] mBaseHexColorSet = {
            "#000000", // BLACK
            "#003399", // BLUE
            "#00cccc", // CYAN
            /*"#444444", // DKGRAY*/
            /*"#888888", // GRAY*/
            "#00cc00", // GREEN
            /*"#cccccc", // LTGRAY*/
            "#cc00cc", // MAGENTA
            "#ff0000", // RED
            /*"#000000", // TRANSPARENT*/
            "#ffffff", // WHITE
            "#ff751a", // ORANGE
    };

//    private static final String[] mBaseHexColorSet = {
//            "#000000", // BLACK
//            "#0000ff", // BLUE
//            "#00ffff", // CYAN
//            /*"#444444", // DKGRAY*/
//            /*"#888888", // GRAY*/
//            "#00ff00", // GREEN
//            /*"#cccccc", // LTGRAY*/
//            "#ff00ff", // MAGENTA
//            "#ff0000", // RED
//            /*"#000000", // TRANSPARENT*/
//            "#ffffff", // WHITE
//            "#ffff00", // YELLOW
//    };

    private static final int NUM_TOTAL_COLOR_BUTTONS = 4;
    private static final int NUM_POSSIBLE_SOLUTION = 4;

    private final Random mRandomGenerator;

    private ArrayList<ColorButton> mCorrectSequence;
    private String[] mStrColorChosen;
    private String[] mHexColorChosen;

    ColorButtonsCreator(int seed, boolean it) {
        if (it) {
            mBaseStringColorSet = mItBaseStringColorSet;
        } else {
            mBaseStringColorSet = mEnBaseStringColorSet;
        }
        mRandomGenerator = new Random(seed);
    }

    List<ColorButton> createColorButtons() {
        mStrColorChosen = getRandomStringColorsSubset();
        mHexColorChosen = getRandomHexColorsSubset();

        ArrayList<ColorButton> colorButtons = createColorButtonsSequence(mStrColorChosen, mHexColorChosen);

        mCorrectSequence = new ArrayList<>(colorButtons);

        Log.d("ColorButtonsCreator", "--- PILOT SEQ ---");
        logSequence(mCorrectSequence);
        Log.d("ColorButtonsCreator", "-------------------");

        Collections.shuffle(colorButtons, mRandomGenerator);

        return colorButtons;
    }

    private ArrayList<ColorButton> createColorButtonsSequence(String[] stringColors, String[] hexColors) {
        ArrayList<ColorButton> result = new ArrayList<>();


        for (int i = 0; i < stringColors.length && i < hexColors.length; i++) {
            result.add(new ColorButton(stringColors[i], hexColors[i]));
        }

        return result;
    }

    private String[] getRandomStringColorsSubset() {
        return getShiftedStringSubset(0);
    }

    private String[] getShiftedStringSubset(int shift) {
        String[] strColorChosen = new String[NUM_TOTAL_COLOR_BUTTONS];

        int i = 0;
        while (i < strColorChosen.length) {
            // take a random string
            int randomIndex = mRandomGenerator.nextInt(mBaseStringColorSet.length);

            int shiftedIndex = (randomIndex + shift) % mBaseStringColorSet.length;

            if (indexNotAlreadyChosen(shiftedIndex, mBaseStringColorSet, strColorChosen)) {
                strColorChosen[i] = mBaseStringColorSet[shiftedIndex];
                i++;
            }
        }

        return strColorChosen;
    }

    private String[] getRandomHexColorsSubset() {
        String[] hexColorChosen = new String[NUM_TOTAL_COLOR_BUTTONS];

        int i = 0;
        while (i < hexColorChosen.length) {
            // Take a random hex
            int randomIndex = mRandomGenerator.nextInt(mBaseHexColorSet.length);

            if (indexNotAlreadyChosen(randomIndex, mBaseHexColorSet, hexColorChosen)) {
                hexColorChosen[i] = mBaseHexColorSet[randomIndex];
                i++;
            }
        }

        return hexColorChosen;
    }

    private boolean indexNotAlreadyChosen(int newIndex, String[] baseColorSet, String[] colorChosen) {
        Log.d("Check into", "--- new check ---");
        for (int i = 0; i < colorChosen.length; i++) {
            if (colorChosen[i] != null) {
                Log.d("Check into", baseColorSet[newIndex] + " == " + colorChosen[i] + " ?");
                if (colorChosen[i].equals(baseColorSet[newIndex])) {
                    Log.d("Check into", "yes");
                    return false;
                } else {
                    Log.d("Check into", "no");
                }
            }
        }
        return true;
    }


    boolean checkPlayerSequence(ArrayList<ColorButton> mPlayerSeqButtonsClicked) {

        for (int i = 0; i < mPlayerSeqButtonsClicked.size(); i++) {
            if (mCorrectSequence.get(i) != mPlayerSeqButtonsClicked.get(i)) {
                return false;
            }
        }

        return true;
    }

    ArrayList<ArrayList<ColorButton>> getPossibleSequences() {
        ArrayList<ArrayList<ColorButton>> possibleCorrectSequences = new ArrayList<>();

        possibleCorrectSequences.add(mCorrectSequence);

        logSequence(mCorrectSequence);
        Log.d("ColorButtonsCreator", "--------------------");

        // fake sequence
        for (int i = 1; i < NUM_POSSIBLE_SOLUTION; i++) {
            possibleCorrectSequences.add(createColorButtonsSequence(getShiftedStringSubset(1), mHexColorChosen));
        }

        Collections.shuffle(possibleCorrectSequences, mRandomGenerator);

        for (ArrayList<ColorButton> sequences : possibleCorrectSequences) {
            logSequence(sequences);
        }

        return possibleCorrectSequences;
    }

    private void logSequence(ArrayList<ColorButton> sequence) {
        Log.d("ColorButtonsCreator", " --- SEQUENCE --");
        for (ColorButton set : sequence) {
            Log.d("ColorButtonsCreator", set.getText() + " - " + set.getColorText());
        }
    }
}
