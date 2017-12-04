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
import android.widget.Toast;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.model.RankedPoiIndex;
import com.codernauti.spaceroverexpress.utils.MapUtils;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Eduard on 16-Nov-17.
 */

class ManualMapState implements MapState {

    private static final String TAG = "ManualMapState";

    private final Context mContext;

    private final MapWrapper mMapWrapper;
    private RankedPoiIndex[] mRankedPoisIndexes;

    private int mDestinationPoiIndex;

    private boolean mDiscoverDialogOpen;

    private GoogleMap.OnCameraMoveListener mCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            updateState(mMapWrapper);
        }
    };

    private final GoogleMap.OnMarkerClickListener mMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            // if the marker is the user -> do nothing
            if (mMapWrapper.isUserMarker(marker)) {
                return true;
            }

            int indexPoi = mMapWrapper.getIndexPoiFromMarker(marker);

            // if marker is destination poi and it's on the edge of display -> do nothing
            if (indexPoi == mDestinationPoiIndex && !(boolean) marker.getTag()) {
                return true;
            }

            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            } else {
                marker.showInfoWindow();
            }
            return true;
        }
    };


    ManualMapState(Context context, final MapWrapper mapWrapper) {
        mContext = context;
        mMapWrapper = mapWrapper;

        mDestinationPoiIndex = SharedPrefUtils.getDestinationPoiIndex(mContext);

        mapWrapper.getGoogleMap().getUiSettings().setAllGesturesEnabled(true);
        mapWrapper.getGoogleMap().setOnMarkerClickListener(mMarkerClickListener);

        mMapWrapper.attachCameraMoveListener(mCameraMoveListener);

        // Difference with GpsMapState
        LatLng lastUserLocation = mMapWrapper.getLastUserPosition();
        if (lastUserLocation == null) {
            Log.d(TAG, "lastUserLocation == null");
            lastUserLocation = MapWrapper.PADUA_LOCATION;
            mMapWrapper.setLastUserLocation(lastUserLocation);
        }

        mMapWrapper.getGoogleMap().animateCamera(
                CameraUpdateFactory.newLatLngZoom(lastUserLocation, MapWrapper.BUILDINGS_ZOOM),
                MapState.CAMERA_COMPASS_ANIMATION_TIME,
                null
        );

        mMapWrapper.animateDestinationPoi();

        Toast.makeText(mContext, R.string.change_to_fly_mode, Toast.LENGTH_SHORT).show();
    }


    private void updateState(MapWrapper mapWrapper) {
        // diference from GpsMapState
        LatLng mCenterCameraPosition = mapWrapper.getGoogleMap().getCameraPosition().target;

        mapWrapper.setLastUserLocation(mCenterCameraPosition);

        mRankedPoisIndexes = MapUtils.getNearestPOIsNotFound(mCenterCameraPosition, MAX_POIS_SHOW + 1);

        if (mDestinationPoiIndex != SharedPrefUtils.DEFAULT_DESTINATION_INT) {
            drawDestinationPOIs(mapWrapper);
        }
        checkNearestPOI(mapWrapper);
    }

    private void drawDestinationPOIs(MapWrapper mapWrapper) {
        mapWrapper.drawEdgePOI(mDestinationPoiIndex);
    }

    private void checkNearestPOI(MapWrapper mapWrapper) {
        if (!mDiscoverDialogOpen) {

            if (mRankedPoisIndexes.length > 0) {
                int nearestPoiIndex = mRankedPoisIndexes[0].getIndex();
                PointOfInterest nearestPoi = mapWrapper.getPOI(nearestPoiIndex);

                if (mRankedPoisIndexes[0].getDistance() <= DISCOVERED_MIN_DISTANCE && !nearestPoi.isFound()) {
                    mDiscoverDialogOpen = true;

                    Log.d(TAG, "Discovered POI: " + mContext.getString(nearestPoi.getTitle()));

                    SharedPrefUtils.saveLastDiscoveredPoiIndex(mContext, nearestPoi.getId());

                    DiscoveredPoiDialog dialogTriggerd = new DiscoveredPoiDialog(mContext, nearestPoi);
                    dialogTriggerd.setCancelable(false);
                    dialogTriggerd.setCanceledOnTouchOutside(false);
                    dialogTriggerd.show();
                }
            }

        }
    }


    @Override
    public void handleNewLocation(Location location) {}

    @Override
    public void handleChangeMode(NewNavigatorFragment fragment, int commandId) {
        freeResources();
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
        return MANUAL_MAP_STATE;
    }

    @Override
    public void freeResources() {
        if (mDestinationPoiIndex != SharedPrefUtils.DEFAULT_DESTINATION_INT) {
            mMapWrapper.restoreMarkersRotation(mDestinationPoiIndex);
        }

        mMapWrapper.detachCameraMoveListener();
        mMapWrapper.cancelAnimations();
    }

}
