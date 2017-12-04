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

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.POIsCreator;
import com.codernauti.spaceroverexpress.model.PointOfInterest;
import com.codernauti.spaceroverexpress.utils.MapUtils;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.VisibleRegion;

class MapWrapper {

    private static final String TAG = "MapWrapper";

    static final float CITY_ZOOM = 14f;
    static final float STREET_ZOOM = 15f;
    static final float BUILDINGS_ZOOM = 20f;

    static final LatLng PADUA_LOCATION = new LatLng(45.4072789,11.8771619); // DEBUG
    private static final LatLngBounds PADUA_BOUNDS = new LatLngBounds(
            new LatLng(45.39049,11.86124),
            new LatLng(45.42014,11.90141)
    );

    private static final float ROTATION_INTO_MAP = 0f;
    private static final float ROTATION_NORTH_EDGE = 180f;
    private static final float ROTATION_EAST_EDGE = 270f;
    private static final float ROTATION_SOUTH_EDGE = 0f;
    private static final float ROTATION_WEST_EDGE = 90f;

    // -----

    private final Context mContext;
    private final GoogleMap mMap;

    private static PointOfInterest[] mPOIs = POIsCreator.getLocations();   // Model objects

    @Nullable
    private LatLng mLastUserLatLng;
    private float mLastUserBearing;

    private LatLng mCameraTopRight;
    private LatLng mCameraBottomLeft;
    private LatLng mCameraBottomRight;
    private LatLng mCameraTopLeft;

    // Map View elements
    private Marker[] mMarkersPool = new Marker[mPOIs.length];
    private Marker mUserLocationMarker;
    private Polygon mPolygonRegion;    // DEBUG


    MapWrapper(GoogleMap map, Context context) {
        mMap = map;
        mContext = context;

        configureMap();
        updatePOIsFromSharedPref();
        initMarkerPool();
    }


    // init

    private void configureMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setLatLngBoundsForCameraTarget(PADUA_BOUNDS);
        mMap.setMinZoomPreference(CITY_ZOOM);
        mMap.setMaxZoomPreference(BUILDINGS_ZOOM);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.mapstyle_my_silver));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(PADUA_LOCATION));
    }

    private void updatePOIsFromSharedPref(){
        for(int i = 0; i < mPOIs.length; i++){
            String key = String.valueOf(mPOIs[i].getId());
            mPOIs[i].setFound(SharedPrefUtils.getBooleanPreference(mContext, key));
        }
        SharedPrefUtils.printLogAllSharedPreferences(mContext);
    }

    // after updatePOIfromSharePref
    private void initMarkerPool(){
        MarkerOptions mPoiMarkerOptions = new MarkerOptions();

        for (int i = 0; i < mPOIs.length; i++) {
            LatLng poi = new LatLng(mPOIs[i].getLatitude(), mPOIs[i].getLongitude());

            mPoiMarkerOptions.title(mContext.getString(mPOIs[i].getTitle()));
            if (mPOIs[i].isFound()) {
                mPoiMarkerOptions.icon(BitmapDescriptorFactory.fromResource(mPOIs[i].getIconMarkerResource()));
            }
            else {
                mPoiMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_question));
            }

            mPoiMarkerOptions.position(poi);

            mMarkersPool[i] = mMap.addMarker(mPoiMarkerOptions);
        }
    }


    // DEBUG methods

    void showLocationLines() {
        for (PointOfInterest poi : mPOIs) {
            LatLng poiLatLng = new LatLng(poi.getLatitude(), poi.getLongitude());

            mMap.addPolygon(
                    new PolygonOptions().add(mLastUserLatLng, poiLatLng)
            );
        }
    }

    void showRegion() {
        if (mPolygonRegion != null) {
            mPolygonRegion.remove();
        }

        mPolygonRegion = mMap.addPolygon(new PolygonOptions()
                .add(
                        mCameraTopRight,
                        mCameraBottomRight,
                        mCameraBottomLeft,
                        mCameraTopLeft
                )
                .fillColor(Color.parseColor("#80FC5959"))
                .strokeWidth(0f)
        );
    }

    void showRegionBounds() {
        if (mCameraTopRight != null && mCameraBottomRight != null &&
                mCameraBottomLeft != null && mCameraTopLeft != null) {
            mMap.addPolygon(new PolygonOptions().add(mCameraTopRight, mCameraBottomRight).strokeColor(Color.YELLOW));
            mMap.addPolygon(new PolygonOptions().add(mCameraBottomRight, mCameraBottomLeft).strokeColor(Color.RED));
            mMap.addPolygon(new PolygonOptions().add(mCameraBottomLeft, mCameraTopLeft).strokeColor(Color.CYAN));
            mMap.addPolygon(new PolygonOptions().add(mCameraTopLeft, mCameraTopRight).strokeColor(Color.BLUE));
        }
    }


    // change map

    void restoreMarkersPositions() {
        for (int i = 0; i < mPOIs.length; i++) {
            LatLng poiLatLng = new LatLng(mPOIs[i].getLatitude(), mPOIs[i].getLongitude());
            mMarkersPool[i].setPosition(poiLatLng);
        }
    }

    void restoreMarkersRotation(int indexMarker) {
        mMarkersPool[indexMarker].setRotation(0f);
    }

    private void updateUserPositionMarker() {
        if (mLastUserLatLng != null) {

            if (mUserLocationMarker == null) {  // lazy initialization
                MarkerOptions mUserMarkerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.rove_marker_2))
                        .position(mLastUserLatLng);

                mUserLocationMarker = mMap.addMarker(mUserMarkerOptions);
            } else {
                mUserLocationMarker.setPosition(mLastUserLatLng);
            }

        }
    }

    // prevent the inconsistent marker position for a previous nearest poi
    void restorePositionForPOI(int poiIndex) {
        mMarkersPool[poiIndex].setPosition(
                new LatLng(mPOIs[poiIndex].getLatitude(), mPOIs[poiIndex].getLongitude())
        );
    }

    void drawEdgePOI(int poiIndex) {
        updateRegionBounds();   // i need the region bounds

        float rotation = ROTATION_INTO_MAP;
        boolean isNotOnEdge = false;
        PointOfInterest poi = mPOIs[poiIndex];
        LatLng poiLatLng = new LatLng(poi.getLatitude(), poi.getLongitude());

        LatLng gtaPoint = MapUtils.intersection(mCameraTopLeft, mCameraTopRight, mLastUserLatLng, poiLatLng);

        if (gtaPoint == null) {
            gtaPoint = MapUtils.intersection(mCameraTopRight, mCameraBottomRight, mLastUserLatLng, poiLatLng);

            if (gtaPoint == null) {
                gtaPoint = MapUtils.intersection(mCameraBottomRight, mCameraBottomLeft, mLastUserLatLng, poiLatLng);

                if (gtaPoint == null) {
                    gtaPoint = MapUtils.intersection(mCameraBottomLeft, mCameraTopLeft, mLastUserLatLng, poiLatLng);

                    if (gtaPoint == null) {
                        gtaPoint = poiLatLng;
                        isNotOnEdge = true;
                    } else {
                        rotation = ROTATION_WEST_EDGE;
                    }
                } else {
                    rotation = ROTATION_SOUTH_EDGE;
                }
            } else {
                rotation = ROTATION_EAST_EDGE;
            }
        } else {
            rotation = ROTATION_NORTH_EDGE;
        }

        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.snippet(mContext.getString(mPOIs[poiIndex].getTitle()))
                .position(gtaPoint)
                .rotation(rotation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(markerOptions);*/

        mMarkersPool[poiIndex].setPosition(gtaPoint);
        mMarkersPool[poiIndex].setRotation(rotation);
        mMarkersPool[poiIndex].setTag(isNotOnEdge);
        Log.d(TAG, "poiIndex update " + poiIndex + " - " + mContext.getString(mPOIs[poiIndex].getTitle()) + " - position: " + gtaPoint.toString());
    }

    private void updateRegionBounds() {
        VisibleRegion region = mMap.getProjection().getVisibleRegion();

        // low ratio => point near region angle
        // high ratio => point near user position
        float ratio = 1f/150f;

        mCameraTopLeft = MapUtils.computePoint(region.farLeft, mLastUserLatLng, ratio);
        mCameraTopRight = MapUtils.computePoint(region.farRight, mLastUserLatLng, ratio);
        mCameraBottomLeft = MapUtils.computePoint(region.nearLeft, mLastUserLatLng, ratio);
        mCameraBottomRight = MapUtils.computePoint(region.nearRight, mLastUserLatLng, ratio);
    }

    void setOriginalIconToPOI(int poiIndex) {
        mMarkersPool[poiIndex].setIcon(BitmapDescriptorFactory.fromResource(mPOIs[poiIndex].getIconMarkerResource()));
    }

    void enableMarkerClick() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    private final Circle[] mCircles = new Circle[CIRCLES_NUM];
    private static final double CONST_RADIUS = 20d;
    private static final int CIRCLES_NUM = 2;
    private static final int CIRCLE_ANIM_DURATION = 5000;

    private ValueAnimator[] mValueAnimator = new ValueAnimator[CIRCLES_NUM];

    void animateDestinationPoi() {
        int destinationPoiIndex = SharedPrefUtils.getDestinationPoiIndex(mContext);

        if (destinationPoiIndex != SharedPrefUtils.DEFAULT_DESTINATION_INT) {
            Marker destinationMarker = mMarkersPool[destinationPoiIndex];

            CircleOptions circleOptions = new CircleOptions()
                    .center(destinationMarker.getPosition())
                    .strokeColor(Color.parseColor("#334d79ff"))
                    .strokeWidth(20f);

            int delayCounter = CIRCLE_ANIM_DURATION / CIRCLES_NUM;

            for (int i = 0; i < mCircles.length; i++) {
                if (mCircles[i] == null) {  // lazy instantiation
                    mCircles[i] = mMap.addCircle(circleOptions);
                } else {
                    mCircles[i].setCenter(destinationMarker.getPosition());
                    mCircles[i].setVisible(true);
                }

                mValueAnimator[i] = buildCircleAnimator(delayCounter * i);
                final int finalI = i;
                mValueAnimator[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mCircles[finalI].setRadius(animation.getAnimatedFraction() * CONST_RADIUS);
                    }
                });
                mValueAnimator[i].start();
            }
        }
    }

    private ValueAnimator buildCircleAnimator(int startDelay) {
        ValueAnimator valueAnimator = new ValueAnimator();

        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setFloatValues(0f, 30f);
        valueAnimator.setDuration(CIRCLE_ANIM_DURATION);
        valueAnimator.setEvaluator(new FloatEvaluator());
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setStartDelay(startDelay);

        return valueAnimator;
    }

    void cancelAnimations() {
        for (int i = 0; i < mValueAnimator.length; i++) {
            if (mCircles[i] != null) {
                mCircles[i].setVisible(false);
            }

            if (mValueAnimator[i] != null) {
                mValueAnimator[i].removeAllUpdateListeners();
                mValueAnimator[i].cancel();
            }
        }
    }


    // Camera Listener

    void attachCameraMoveListener(GoogleMap.OnCameraMoveListener listener) {
        if (mMap != null) {
            mMap.setOnCameraMoveListener(listener);
        }
    }

    void detachCameraMoveListener() {
        if (mMap != null) {
            mMap.setOnCameraMoveListener(null);
        }
    }

    // Getter and setter

    void updateLastLocation(Location location) {

        if(mLastUserLatLng != null){
            float distanceAtoB = MapUtils.getDistance(mLastUserLatLng.latitude, mLastUserLatLng.longitude,
                    location.getLatitude(), location.getLongitude());
            SharedPrefUtils.updateDistanceTraveled(mContext, distanceAtoB);
        }

        mLastUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mLastUserBearing = location.getBearing();

        updateUserPositionMarker();
    }

    void setLastUserLocation(LatLng lastUserLocation) {
        mLastUserLatLng = lastUserLocation;

        updateUserPositionMarker();
    }

    @Nullable
    LatLng getLastUserPosition() {
        return mLastUserLatLng;
    }

    float getLastUserBearing() {
        return mLastUserBearing;
    }

    GoogleMap getGoogleMap() {
        return mMap;
    }

    PointOfInterest getPOI(int poiIndex) {
        return mPOIs[poiIndex];
    }

    int getIndexPoiFromMarker(Marker markerToFind) {
        for (int i = 0; i < mMarkersPool.length; i++) {
            if (mMarkersPool[i].getId().equals(markerToFind.getId()) /*|| mMarkersPool[i].equals(markerToFind)*/) {
                Log.d(TAG, "marker index found of " + mMarkersPool[i].getId() + " " + markerToFind.getId());
                return i;
            }
        }

        Log.e(TAG, "Marker not find into Map!");
        return 0;
    }

    boolean isUserMarker(Marker marker) {
        return marker.equals(mUserLocationMarker);
    }
}
