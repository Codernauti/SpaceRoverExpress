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
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AboveMapState implements MapState, InfoMarkerPoiDialog.Listener {

    private static final String TAG = "AboveMapState";

    private final MapWrapper mMapWrapper;;
    private Context mContext;

    private InfoMarkerPoiDialog mDialog;

    private int mPoiIndex;

    private final GoogleMap.OnMarkerClickListener mMarkerClickListener  = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            // if the marker is the user do nothing
            if (mMapWrapper.isUserMarker(marker)) {
                return true;
            }

            mPoiIndex = mMapWrapper.getIndexPoiFromMarker(marker);
            mDialog = new InfoMarkerPoiDialog(mContext, mMapWrapper.getPOI(mPoiIndex));

            CameraPosition newCameraPosition = mAboveCameraPos
                    .target(marker.getPosition())
                    .zoom(MapWrapper.BUILDINGS_ZOOM - 3f)
                    .build();

            mMapWrapper.getGoogleMap().animateCamera(
                            CameraUpdateFactory.newCameraPosition(newCameraPosition),
                            CAMERA_DESTINATION_ANIMATION_TIME,
                            new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    mDialog.setListener(AboveMapState.this);
                                    mDialog.show();
                                }

                                @Override
                                public void onCancel() { }
                            });

            return true;
        }
    };

    private final CameraPosition.Builder mAboveCameraPos = new CameraPosition.Builder()
            .bearing(0f)
            .tilt(0f)
            .zoom(MapWrapper.STREET_ZOOM);


    AboveMapState(MapWrapper mapWrapper, Context context) {
        Log.d(TAG, "\n ---- AboveMapState\n");
        mMapWrapper = mapWrapper;
        mContext = context;

        mapWrapper.detachCameraMoveListener();
        mapWrapper.restoreMarkersPositions();

        mapWrapper.getGoogleMap().getUiSettings().setAllGesturesEnabled(true);
        mapWrapper.getGoogleMap().getUiSettings().setTiltGesturesEnabled(false);
        mapWrapper.enableMarkerClick();

        mapWrapper.getGoogleMap().setOnMarkerClickListener(mMarkerClickListener);

        if(mapWrapper.getLastUserPosition() != null) {
            mapWrapper.getGoogleMap()
                    .animateCamera(CameraUpdateFactory.newCameraPosition(
                            mAboveCameraPos.target(mapWrapper.getLastUserPosition()).build()),
                            CAMERA_ANIMATION_TIME,
                            null);
        }

        mMapWrapper.animateDestinationPoi();
    }


    @Override
    public void handleNewLocation(Location location) {
        mMapWrapper.updateLastLocation(location);
        // TODO: Use only for DEBUG
        //mMapWrapper.setLastUserLocation(MapWrapper.PADUA_LOCATION);
    }

    @Override
    public void handleChangeMode(NewNavigatorFragment fragment, int commandId) {
        freeResources();

        switch (commandId) {
            case R.id.change_view_mode: {
                if (SharedPrefUtils.getBooleanPreference(mContext, PrefKey.MANUAL_MAP_STATE)) {
                    fragment.startLocationUpdates();
                    fragment.setMapState(new ManualMapState(fragment.getContext(), mMapWrapper));
                    fragment.setZoomButtonMode(MapState.zoomIn);
                } else  {
                    fragment.setMapState(new GpsMapState(fragment.getContext(), mMapWrapper));
                    fragment.setZoomButtonMode(MapState.zoomOut);
                    //Toast.makeText(mContext, R.string.change_to_gps_map_view, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public int getStateId() {
        return ABOVE_MAP_STATE;
    }

    @Override
    public void freeResources() {
        mMapWrapper.cancelAnimations();
    }

    // Dialog listener

    @Override
    public void onGuideClicked() {
        SharedPrefUtils.saveDestinationPoiIndex(mContext, mPoiIndex);
        mMapWrapper.cancelAnimations();
        mMapWrapper.animateDestinationPoi();
    }
}
