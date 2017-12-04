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

package com.codernauti.spaceroverexpress.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.codernauti.spaceroverexpress.game.GameEndActivity;
import com.codernauti.spaceroverexpress.model.PointOfInterest;

import java.util.Map;

public class SharedPrefUtils {

    final static String TAG = "SharedPrefUtils";

    public final static boolean DEFAULT_BOOLEAN_VALUE = false;
    public final static int DEFAULT_INT_VALUE = 0;
    public final static int DEFAULT_DESTINATION_INT = -1;

    public static void saveLongPreference(Context context, String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(key, value)
                .apply();
    }

    public static long getLongPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(key, 0);
    }

    public static void saveFloatPreference(Context context, String key, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(key, value)
                .apply();
    }

    public static float getFloatPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(key, 0);
    }

    public static void saveBooleanPreference(Context context, String key, boolean value) {
        Log.d(TAG, "Save boolean pref " + key + ": " + value);

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static boolean getBooleanPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }

    public static void clearSharedPreferences(Context context) {
        Log.d(TAG, "SharedPreferences cleared");
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    public static void printLogAllSharedPreferences(Context context) {
        Map<String,?> keys = PreferenceManager.getDefaultSharedPreferences(context).getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    public static int getIntPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, DEFAULT_INT_VALUE);
    }

    public static void updateDistanceTraveled(Context context, float meters){
        float distancetraveledSaved = getFloatPreference(context, PrefKey.TOTAL_METERS_TRAVELED);
        saveFloatPreference(context, PrefKey.TOTAL_METERS_TRAVELED,distancetraveledSaved + meters);

        if(distancetraveledSaved >= 5000 ){
            if(!getBooleanPreference(context, PrefKey.HALLEY_BADGE)){ // if not saved
                saveBooleanPreference(context, PrefKey.HALLEY_BADGE, true);
            }
        }
    }

    public static void saveLastDiscoveredPoiIndex(Context context, int poiId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PrefKey.LAST_DISCOVERED_POI, poiId)
                .apply();
    }

    public static int getLastDiscoveredPoiIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PrefKey.LAST_DISCOVERED_POI, 0);
    }

    public static void saveDestinationPoiIndex(Context context, int poiId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PrefKey.DESTINATION_POI, poiId)
                .apply();
    }

    public static int getDestinationPoiIndex(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PrefKey.DESTINATION_POI, DEFAULT_DESTINATION_INT);
    }

    public static void incrementRoverLevel(Context context){
        int actuaLevel =  PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PrefKey.ROVER_LEVEL, 0);

        actuaLevel++;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PrefKey.ROVER_LEVEL, actuaLevel)
                .apply();
    }
}
