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

package com.codernauti.spaceroverexpress.navigator;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.model.RankedPoiIndex;
import com.codernauti.spaceroverexpress.utils.MapUtils;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

class GpsNearestMapState implements MapState {

    private static final String TAG = "GpsNearestMapState";

    private final Context mContext;
    private final MapWrapper mMapWrapper;

    private RankedPoiIndex[] mRankedPoisIndexes;

    private boolean mDiscoverDialogOpen;

    private final CameraPosition.Builder mStandardCameraBuilder = new CameraPosition.Builder()
            .tilt(60f)
            .zoom(MapWrapper.BUILDINGS_ZOOM);

    private final GoogleMap.OnMarkerClickListener mMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int indexPoi = mMapWrapper.getIndexPoiFromMarker(marker);

            for (RankedPoiIndex rankedPoi : mRankedPoisIndexes) {
                if (indexPoi == rankedPoi.getIndex() && (boolean)marker.getTag()) {
                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                    } else {
                        marker.showInfoWindow();
                    }
                }
            }
            return true;
        }
    };

    private final GoogleMap.OnCameraMoveListener mCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            updateState(mMapWrapper);
        }
    };


    GpsNearestMapState(Context context, final MapWrapper mapWrapper) {
        Log.d(TAG, "\n ---- GpsNearestMapState\n");
        mContext = context;
        mMapWrapper = mapWrapper;

        mapWrapper.getGoogleMap().getUiSettings().setAllGesturesEnabled(false);
        mapWrapper.getGoogleMap().setOnMarkerClickListener(mMarkerClickListener);

        mMapWrapper.attachCameraMoveListener(mCameraMoveListener);

        /*// restore image on user location
        LatLng lastUserLocation = mMapWrapper.getLastUserPosition();
        if (lastUserLocation != null) {
            if (!mCompass.isActive()) {
                mCompass.registerListener();
            }
        }*/
    }


    private void updateState(MapWrapper mapWrapper) {
        if (mapWrapper.getLastUserPosition() != null) {

            mRankedPoisIndexes = MapUtils.getNearestPOIsNotFound(mapWrapper.getLastUserPosition(), MAX_POIS_SHOW + 1);

            if (mRankedPoisIndexes.length > 5) {
                mapWrapper.restorePositionForPOI(mRankedPoisIndexes[5].getIndex());
            }
            drawRankedPOIs(mapWrapper);
            checkNearestPOI(mapWrapper);
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        mMapWrapper.updateLastLocation(location);
        // trigger onCameraMoveListener
        mMapWrapper.getGoogleMap().animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        mStandardCameraBuilder.target(mMapWrapper.getLastUserPosition())
                                .bearing(mMapWrapper.getLastUserBearing())
                                .build()),
                CAMERA_ANIMATION_TIME,
                null
        );
    }

    @Override
    public void handleChangeMode(NewNavigatorFragment fragment, int commandId) {
        // free resources
        if (mRankedPoisIndexes != null) {
            for (RankedPoiIndex mRankedPoi : mRankedPoisIndexes) {
                mMapWrapper.restoreMarkersRotation(mRankedPoi.getIndex());
            }
        }
        mMapWrapper.detachCameraMoveListener();

        switch (commandId) {
            case R.id.change_view_mode: {
                fragment.setMapState(new AboveMapState(mMapWrapper, mContext));
                fragment.setZoomButtonMode(MapState.zoomOut);
                break;
            }
        }
    }

    @Override
    public int getStateId() {
        return GPS_MAP_STATE;
    }

    @Override
    public void freeResources() {

    }

    private void drawRankedPOIs(MapWrapper mapWrapper) {
        if (mRankedPoisIndexes.length > MAX_POIS_SHOW) {    // there are N marker showed and 1 must be restored
            Log.d(TAG, "mRankedPois length is " + String.valueOf(MAX_POIS_SHOW + 1));
            for (int i = 0; i < mRankedPoisIndexes.length - 1; i++) {
                mapWrapper.drawEdgePOI(mRankedPoisIndexes[i].getIndex());
            }
        } else {    // there are less than MAX_POIS_SHOW showed
            Log.d(TAG, "mRankedPois length is " + mRankedPoisIndexes.length);
            for (int i = 0; i < mRankedPoisIndexes.length; i++) {
                mapWrapper.drawEdgePOI(mRankedPoisIndexes[i].getIndex());
            }
        }
    }

    private void checkNearestPOI(MapWrapper mapWrapper) {
        if (!mDiscoverDialogOpen) {

            if (mRankedPoisIndexes.length > 0) {
                int nearestPoiIndex = mRankedPoisIndexes[0].getIndex();
                PointOfInterest nearestPoi = mapWrapper.getPOI(nearestPoiIndex);

                if (mRankedPoisIndexes[0].getDistance() <= DISCOVERED_MIN_DISTANCE && !nearestPoi.isFound()) {
                    mDiscoverDialogOpen = true;

                    // Log.d(TAG, "Discovered POI: " + mContext.getString(nearestPoi.getTitle()));

                    // TODO: get callback from dismiss
                    new DiscoveredPoiDialog(mContext, nearestPoi).show();

                    SharedPrefUtils.saveLastDiscoveredPoiIndex(mContext, nearestPoi.getId());
                }
            }
        }
    }

}
