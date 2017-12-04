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

package com.codernauti.spaceroverexpress.game.decrypto;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

class DecryptoCreator {

    private static final String TAG = "DecryptoCreator";

    private int mNum_chars;
    private int mLength;

    private String[] SYMBOLS = {"Ψ", "Δ", "Ͽ" , "▲", "H", "ᴥ", "m", "M", "Σ", "β",
            "Φ", "Ш", "Ж", "Ω", "∀", "‡", ">", "©", "§", "Θ" };

    private final int mSeed;
    private final Random mRandomGenerator;

    DecryptoCreator(int seed, int numChar) {
        mNum_chars = numChar;

        mSeed = seed;
        mRandomGenerator = new Random(mSeed);
    }


    public ArrayList<CryptedChar> createCryptedChars() {

        mLength = SYMBOLS.length;
        ArrayList<CryptedChar> cryptedChars = new ArrayList<>();

        ArrayList<Integer> numberList = new ArrayList<>();
        ArrayList<String> charList = new ArrayList<>();

        for(int i = 0 ; i < mLength; i++ ){
           charList.add(SYMBOLS[i]);
        }

        int v = 0;

        for(int i = 0 ; i < mLength; i++ ){
            if(v == 10) v = 0;
            numberList.add(v);
            v++;
        }

        Collections.shuffle(numberList, mRandomGenerator);
        Collections.shuffle(charList, mRandomGenerator);

        for(int i = 0; i < mLength; i++){
            cryptedChars.add(new CryptedChar(charList.get(i), numberList.get(i)));
        }

        return cryptedChars;
    }


    public boolean checkCombination(ArrayList<Integer> combinationList, ArrayList<CryptedChar> cryptedChars) {
        boolean isCorrect = true;
        int i = 0;
        while(isCorrect && i < mNum_chars){
            if(combinationList.get(i) != cryptedChars.get(i).getValue()){
                isCorrect = false;
            }
            i++;
        }
        Log.d(TAG, "isCorrect : "+ isCorrect);
        return  isCorrect;
    }

    public int getNumSymbols(){
        return SYMBOLS.length;
    }
}
