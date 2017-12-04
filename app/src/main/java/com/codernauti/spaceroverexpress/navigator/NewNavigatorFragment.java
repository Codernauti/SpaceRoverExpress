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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.PrefKey;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class NewNavigatorFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = NewNavigatorFragment.class.getSimpleName();

    private static final String MAP_STATE_KEY = "navigator_state_key";

    // Gps options
    private static final long GPS_INTERVAL = 5000;
    private static final long GPS_FASTEST_INTERVAL = 2000;
    private static final int GPS_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;


    private static final int REQUEST_CHECK_SETTINGS = 100;

    // View fields
    private MapView mMapView;
    private FloatingActionButton mChangeButton;

    // Location Service from GoogleApi
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private boolean mLocationUpdateEnable;


    // --------- REFACTORING AREA ----------------

    private MapWrapper mMapWrapper;

    private MapState mMapState;
    private int mPrevState;

    // --------------------------------------------

    // Listeners and callback (no lazy evaluation)

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.d(TAG, "new Location of user found!");
                mMapState.handleNewLocation(location);
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mPrevState = savedInstanceState.getInt(MAP_STATE_KEY);
        } else {
            mPrevState = MapState.GPS_MAP_STATE;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.new_nav_frag, container, false);

        mChangeButton = root.findViewById(R.id.change_view_mode);
        mChangeButton.setOnClickListener(this);

        mMapView = root.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mFusedLocationClient = new FusedLocationProviderClient(getContext());

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (!SharedPrefUtils.getBooleanPreference(getContext(), PrefKey.MANUAL_MAP_STATE) && mPrevState == MapState.MANUAL_MAP_STATE) {
            mPrevState = MapState.GPS_MAP_STATE;
        }

        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mMapView.onResume();

        if (!isConnected()) {
            Toast.makeText(getContext(), R.string.offline_msg, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void startLocationUpdates() {
        if (mPrevState != MapState.MANUAL_MAP_STATE) {
            try {
                if (!mLocationUpdateEnable) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    mLocationUpdateEnable = true;
                }
                askToActiveGps();

                if (mLocationUpdateEnable) {
                    // update from last location
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        Log.d(TAG, "onLastLocation");
                                        mMapState.handleNewLocation(location);
                                    }
                                }
                            });
                }

            } catch (SecurityException e) {
                Log.e(TAG, "Permission denied", e);
            }
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");

        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        stopLocationUpdates();
        mMapState.freeResources();

        super.onStop();
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mLocationUpdateEnable = false;
        }
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    // Persistence fragment state

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMapState != null) {
            outState.putInt(MAP_STATE_KEY, mMapState.getStateId());
        }
    }


    // Map callbacks

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMapWrapper = new MapWrapper(googleMap, getContext());

        instantiateState();
    }

    private void instantiateState() {
        if (mPrevState == MapState.GPS_MAP_STATE &&
                SharedPrefUtils.getBooleanPreference(getContext(), PrefKey.MANUAL_MAP_STATE)) {
            mPrevState = MapState.MANUAL_MAP_STATE;
        }

        switch (mPrevState) {
            case MapState.MANUAL_MAP_STATE: {
                mMapState = new ManualMapState(getContext(), mMapWrapper);
                break;
            }
            case MapState.GPS_MAP_STATE: {
                mMapState = new GpsMapState(getContext(), mMapWrapper);
                break;
            }
            case MapState.ABOVE_MAP_STATE: {
                mMapState = new AboveMapState(mMapWrapper, getContext());
                break;
            }
        }
    }


    // GoogleApiClient callbacks

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        NewNavigatorFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    private void askToActiveGps() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Log.d(TAG, "onSuccess");
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure");

                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case CommonStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(NewNavigatorFragment.this.getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way
                                // to fix the settings so we won't show the dialog.
                                break;
                        }
                    }
                });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(GPS_INTERVAL);
        mLocationRequest.setFastestInterval(GPS_FASTEST_INTERVAL);
        mLocationRequest.setPriority(GPS_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    // Permission management

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        NewNavigatorFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForAccessFineLocation (final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setMessage(getContext().getString(R.string.gps_dialog_alert))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForAccessFineLocation() {
        Toast.makeText(getContext(), "Permessi posizione negati", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForAccessFineLocation() {
        Toast.makeText(getContext(), "I Permessi non verranno più richiesti", Toast.LENGTH_SHORT).show();
    }


    // onClickListener callbacks

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_view_mode: {
                mMapState.handleChangeMode(this, R.id.change_view_mode);
                break;
            }
        }
    }

    void setMapState(MapState mapState) {
        this.mMapState = mapState;
    }

    void setZoomButtonMode(int resource){
        mChangeButton.setImageDrawable(ContextCompat.getDrawable(getContext(), resource));
    }

}
