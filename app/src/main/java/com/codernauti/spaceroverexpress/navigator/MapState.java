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

import android.location.Location;

import com.codernauti.spaceroverexpress.R;

public interface MapState {

    public static final int zoomOut = R.drawable.ic_zoom_out_map_white_24dp;
    public static final int zoomIn = R.drawable.ic_fullscreen_exit_white_24dp;

    int MANUAL_MAP_STATE = 0;
    int GPS_MAP_STATE = 1;
    int ABOVE_MAP_STATE = 2;

    int MAX_POIS_SHOW = 5;

    int CAMERA_ANIMATION_TIME = 250;
    int CAMERA_DESTINATION_ANIMATION_TIME = 100;
    int CAMERA_COMPASS_ANIMATION_TIME = 90;

    float DISCOVERED_MIN_DISTANCE = 10f;   // in meters

    void handleNewLocation(Location location);

    void handleChangeMode(NewNavigatorFragment fragment, int commandId);

    int getStateId();

    void freeResources();
}
