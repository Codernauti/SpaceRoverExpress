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
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.model.RankedPoiIndex;
import com.codernauti.spaceroverexpress.tutorial.TutorialDialogFragment;
import com.codernauti.spaceroverexpress.tutorial.TutorialMessages;
import com.codernauti.spaceroverexpress.utils.MapUtils;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.hoan.dsensor_master.DProcessedSensor;
import com.hoan.dsensor_master.DSensorEvent;
import com.hoan.dsensor_master.DSensorManager;
import com.hoan.dsensor_master.interfaces.DProcessedEventListener;

class GpsMapState implements MapState/*, Compass.CompassListener*/ {

    private static final String TAG = "GpsMapState";

    private final Context mContext;
    private final MapWrapper mMapWrapper;
    //private final Compass mCompass;

    private RankedPoiIndex[] mRankedPoisIndexes;

    private int mDestinationPoiIndex;

    private boolean mDiscoverDialogOpen;

    private final CameraPosition.Builder mStandardCameraBuilder = new CameraPosition.Builder()
            .tilt(60f)
            .zoom(MapWrapper.BUILDINGS_ZOOM);

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

    private final GoogleMap.OnCameraMoveListener mCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            updateState(mMapWrapper);
        }
    };


    GpsMapState(Context context, final MapWrapper mapWrapper) {
        Log.d(TAG, "\n ---- GpsMapState\n");
        mContext = context;
        mMapWrapper = mapWrapper;

        //mCompass = new Compass((SensorManager)context.getSystemService(Context.SENSOR_SERVICE));
        //mCompass.setListener(this);

        mDestinationPoiIndex = SharedPrefUtils.getDestinationPoiIndex(mContext);

        mapWrapper.getGoogleMap().getUiSettings().setAllGesturesEnabled(false);
        mapWrapper.getGoogleMap().setOnMarkerClickListener(mMarkerClickListener);

        mMapWrapper.attachCameraMoveListener(mCameraMoveListener);

        // restore image on user location
        LatLng lastUserLocation = mMapWrapper.getLastUserPosition();
        if (lastUserLocation != null) {
            if (SharedPrefUtils.getBooleanPreference(mContext, PrefKey.MAP_COMPASS)) {
                attachCompass();
            } else {
                moveCamera(mMapWrapper.getLastUserBearing());
            }
        }

        mMapWrapper.animateDestinationPoi();
    }

    private void attachCompass() {
        /*if (!mCompass.isActive()) {
            mCompass.registerListeners();
        }*/
        DSensorManager.startDProcessedSensor(mContext, DProcessedSensor.TYPE_3D_COMPASS,
                new DProcessedEventListener() {
                    @Override
                    public void onProcessedValueChanged(DSensorEvent dSensorEvent) {
                        // update UI
                        // dSensorEvent.values[0] is the azimuth.
                        float azimuth = (float) Math.toDegrees(dSensorEvent.values[0]);
                        moveCamera(azimuth);
                    }
                });
    }

    private void moveCamera(float bearing) {
        Log.d(TAG, "Orientation:" + bearing + " userLocation: " + mMapWrapper.getLastUserPosition());

        mMapWrapper.getGoogleMap().animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        mStandardCameraBuilder.target(mMapWrapper.getLastUserPosition())
                                .bearing(bearing)
                                .build()),
                MapState.CAMERA_COMPASS_ANIMATION_TIME,
                null
        );
    }


    private void updateState(MapWrapper mapWrapper) {
        if (mapWrapper.getLastUserPosition() != null) {

            mRankedPoisIndexes = MapUtils.getNearestPOIsNotFound(mapWrapper.getLastUserPosition(), MAX_POIS_SHOW + 1);

            if (mDestinationPoiIndex != SharedPrefUtils.DEFAULT_DESTINATION_INT) {
                drawDestinationPOIs(mapWrapper);
            }
            checkNearestPOI(mapWrapper);
        }
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

                    // Log.d(TAG, "Discovered POI: " + mContext.getString(nearestPoi.getTitle()));

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
    public void handleNewLocation(Location location) {
        mMapWrapper.updateLastLocation(location);
        // TODO: Use only for DEBUG
        //mMapWrapper.setLastUserLocation(MapWrapper.PADUA_LOCATION);
        if (SharedPrefUtils.getBooleanPreference(mContext, PrefKey.MAP_COMPASS)) {
            attachCompass();
        } else {
            moveCamera(location.getBearing());
        }
    }

    @Override
    public void handleChangeMode(NewNavigatorFragment fragment, int commandId) {
        freeResources();

        switch (commandId) {
            case R.id.change_view_mode: {
                fragment.setMapState(new AboveMapState(mMapWrapper, mContext));
                fragment.setZoomButtonMode(MapState.zoomIn);

                if(!SharedPrefUtils.getBooleanPreference(mContext, PrefKey.FIRST_OPEN_ABOVE)) {
                    TutorialDialogFragment dialogTutorial =
                            new TutorialDialogFragment(mContext,
                                    PrefKey.FIRST_OPEN_ABOVE,
                                    TutorialMessages.TutorialFirstAbove,
                                    false);
                    dialogTutorial.setCancelable(false);
                    dialogTutorial.setCanceledOnTouchOutside(false);
                    dialogTutorial.show();
                }

                Toast.makeText(mContext, R.string.change_to_above_map_view, Toast.LENGTH_SHORT).show();
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
        if (mDestinationPoiIndex != SharedPrefUtils.DEFAULT_DESTINATION_INT) {
            mMapWrapper.restoreMarkersRotation(mDestinationPoiIndex);
        }

        mMapWrapper.detachCameraMoveListener();
        mMapWrapper.cancelAnimations();

        //mCompass.unregisterListeners();
        DSensorManager.stopDSensor();
    }


    // Compass Callback

    /*@Override
    public void changed(float orientation) {
         moveCamera(orientation);
    }*/
}
