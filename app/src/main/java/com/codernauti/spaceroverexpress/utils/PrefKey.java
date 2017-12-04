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

public interface PrefKey {

    // Options
    String MANUAL_MAP_STATE = "manual_map_state";
    String MAP_COMPASS = "map_compass";
    String PILOT_ASSISTANT_MODE = "pilot_assistant_mode";

    String TOTAL_SCORE = "total_score";
    String TOTAL_METERS_TRAVELED = "total_meters_traveled";
    String LOCATIONS_SAVED = "locations_saved";
    String ROVER_LEVEL = "rover_level";
    String EXPERIENCE = "experience";

    // Badges
    String HALLEY_BADGE = "halley_badge";
    String TRAVELER_BADGE = "traveler_badge";
    String TERRAPIATTISTA_BADGE = "terrapiattista_badge";

    String LAST_DISCOVERED_POI = "last_discovered_poi";
    String LAST_DISCOVERED_ANIMATE = "last_discovered_poi_seen";
    String DESTINATION_POI = "destination_poi";


    // Tutorial
    // Game
    String FIRST_OPEN_GAME = "first_open_game";
    // Firs Access
    String FIRST_OPEN_ROLE  = "first_open_role";
    // Map
    String FIRST_OPEN_MAP = "first_open_map";
    String FIRST_OPEN_ABOVE = "first_open_above";
}
