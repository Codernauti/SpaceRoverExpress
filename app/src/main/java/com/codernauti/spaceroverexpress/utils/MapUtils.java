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

import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

import com.codernauti.spaceroverexpress.model.POIsCreator;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.model.RankedPoiIndex;
import com.codernauti.spaceroverexpress.navigator.NewNavigatorFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtils{

    final static String TAG = "MapUtils";

    private static PointOfInterest[] mPOIs = POIsCreator.getLocations();

    public static float getDistance(double startLatitude, double startLongitude, double destLatitude, double destLongitude) {

        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, destLatitude, destLongitude, results);
        float distanceInMeters = results[0];

        return distanceInMeters;
    }

    public static RankedPoiIndex[] getRankNearestLocations(LatLng userLatLng){

        Map<Integer, Float> unsortFloatMap = new HashMap<Integer, Float>();
        for(int i = 0; i < mPOIs.length; i++){
            float distance = getDistance(userLatLng.latitude, userLatLng.longitude, mPOIs[i].getLatitude(), mPOIs[i].getLongitude());
            unsortFloatMap.put(i, distance);
        }

        Map<Integer, Float> sortedFloatMap = sortByFloatValue(unsortFloatMap);

        RankedPoiIndex[] rankedPOIsIndexes = new RankedPoiIndex[mPOIs.length];
        int j = 0;

        for (Map.Entry<Integer, Float> entry : sortedFloatMap.entrySet())
        {
            rankedPOIsIndexes[j] = new RankedPoiIndex(entry.getKey(), entry.getValue() );
            j++;
        }

        return  rankedPOIsIndexes;
    }

    private static Map<Integer, Float> sortByFloatValue(Map<Integer, Float> unsortMap) {
        List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
            public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        /// Loop the sorted list and put it into a new insertion order Map
        /// LinkedHashMap
        Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
        for (Map.Entry<Integer, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    private static ArrayList<RankedPoiIndex> mTempRankedPois = new ArrayList<>();
    private static Comparator<RankedPoiIndex> mRankedPoiComparator = new Comparator<RankedPoiIndex>() {
        @Override
        public int compare(RankedPoiIndex rankedPoi1, RankedPoiIndex rankedPoi2) {
            if (rankedPoi1.getDistance() > rankedPoi2.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    public static RankedPoiIndex[] getNearestPOIs(LatLng userLatLng, int maxNearestPois) {
        mTempRankedPois.clear();

        for (int i = 0; i < mPOIs.length; i++) {
            float distanceFromUser = getDistance(userLatLng.latitude, userLatLng.longitude,
                    mPOIs[i].getLatitude(), mPOIs[i].getLongitude());

            mTempRankedPois.add(new RankedPoiIndex(i, distanceFromUser));
        }

        Collections.sort(mTempRankedPois, mRankedPoiComparator);

        RankedPoiIndex[] nearestPoisIndex = new RankedPoiIndex[maxNearestPois];
        for (int i = 0; i < nearestPoisIndex.length; i++) {
            nearestPoisIndex[i] = mTempRankedPois.get(i);
            //Log.d("NearestPoisIndexes", nearestPoisIndex[i].toString());
        }

        return nearestPoisIndex;
    }

    public static RankedPoiIndex[] getNearestPOIsNotFound(LatLng userLatLng, int maxNearestPois) {
        mTempRankedPois.clear();

        for (int i = 0; i < mPOIs.length; i++) {
            float distanceFromUser = getDistance(userLatLng.latitude, userLatLng.longitude,
                    mPOIs[i].getLatitude(), mPOIs[i].getLongitude());

            if (!mPOIs[i].isFound()) {
                mTempRankedPois.add(new RankedPoiIndex(i, distanceFromUser));
            }
        }

        Collections.sort(mTempRankedPois, mRankedPoiComparator);

        // we limit nearestPoisIndex only if its size is greater than maxNearestPois
        int nearestPoisLenght = mTempRankedPois.size() >= maxNearestPois ? maxNearestPois : mTempRankedPois.size();

        RankedPoiIndex[] nearestPoisIndex = new RankedPoiIndex[nearestPoisLenght];
        for (int i = 0; i < nearestPoisIndex.length; i++) {
            nearestPoisIndex[i] = mTempRankedPois.get(i);
            //Log.d("NearestPoisIndexes", nearestPoisIndex[i].toString());
        }

        return nearestPoisIndex;
    }

    public static RankedPoiIndex[] getAllPOIS() {
        RankedPoiIndex[] result = new RankedPoiIndex[mPOIs.length];

        for (int i = 0; i < mPOIs.length; i++) {
            result[i] = new RankedPoiIndex(i, 0);
        }

        return result;
    }


    public static LatLng intersection(LatLng segStart, LatLng segEnd, LatLng userLatLng, LatLng point) {
        double x1 = segStart.longitude;
        double y1 = segStart.latitude;
        double x2 = segEnd.longitude;
        double y2 = segEnd.latitude;
        double x3 = userLatLng.longitude;
        double y3 = userLatLng.latitude;
        double x4 = point.longitude;
        double y4 = point.latitude;

        double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);

        if (d == 0) return null;

        double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
        double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;

        LatLng intersection = new LatLng(yi, xi);

        if (xi > Math.min(x3, x4) && xi < Math.max(x3, x4)
                && xi > Math.min(x1, x2) && xi < Math.max(x1, x2)) {
            return intersection;
        } else {
            return null;
        }
    }

    public static LatLng computePoint(LatLng a, LatLng b, float ratio) {
        return new LatLng(
                (float) a.latitude + ratio * (b.latitude - a.latitude),
                (float) a.longitude + ratio * (b.longitude - a.longitude));
    }
}
