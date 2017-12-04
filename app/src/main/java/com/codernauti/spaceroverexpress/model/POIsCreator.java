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

package com.codernauti.spaceroverexpress.model;

import com.codernauti.spaceroverexpress.R;

public class POIsCreator {

    private static final String TAG = "POIsCreator";

    private static PointOfInterest[] mPointOfInterests = {
            new PointOfInterest(0, R.drawable.round_1, R.drawable.rect_1, R.string.title_location_1, R.string.subtitle_location_1, R.string.description_location_1,45.3984133, 11.8765003, false, R.drawable.marker_phanteon),
            new PointOfInterest(1, R.drawable.round_2, R.drawable.rect_2, R.string.title_location_2, R.string.subtitle_location_2, R.string.description_location_2,45.401267, 11.880161, false, R.drawable.marker_church),
            new PointOfInterest(2, R.drawable.round_3, R.drawable.rect_3, R.string.title_location_3, R.string.subtitle_location_3, R.string.description_location_3,45.403454, 11.881769, false, R.drawable.marker_phanteon),
            new PointOfInterest(3, R.drawable.round_4, R.drawable.rect_4, R.string.title_location_4, R.string.subtitle_location_4, R.string.description_location_4,45.405018, 11.880906, false, R.drawable.marker_church),
            new PointOfInterest(4, R.drawable.round_5, R.drawable.rect_5, R.string.title_location_5, R.string.subtitle_location_5, R.string.description_location_5,45.407371, 11.884457, false, R.drawable.marker_church),
            new PointOfInterest(5, R.drawable.round_6, R.drawable.rect_6, R.string.title_location_6, R.string.subtitle_location_6, R.string.description_location_6,45.408268, 11.881786, false, R.drawable.marker_phanteon),
            new PointOfInterest(6, R.drawable.round_7, R.drawable.rect_7, R.string.title_location_7, R.string.subtitle_location_7, R.string.description_location_7,45.410626, 11.879129, false, R.drawable.marker_church),
            new PointOfInterest(7, R.drawable.round_8, R.drawable.rect_8, R.string.title_location_8, R.string.subtitle_location_8, R.string.description_location_8,45.412007, 11.879629, false, R.drawable.marker_church),
            new PointOfInterest(8, R.drawable.round_9, R.drawable.rect_9, R.string.title_location_9, R.string.subtitle_location_9, R.string.description_location_9,45.407772, 11.877194, false, R.drawable.marker_phanteon),
            new PointOfInterest(9, R.drawable.round_10, R.drawable.rect_10, R.string.title_location_10, R.string.subtitle_location_10, R.string.description_location_10,45.407797, 11.877453, false, R.drawable.marker_phanteon), //TODO attenzione è troppo vicino al pedrocchi!
            new PointOfInterest(10, R.drawable.round_11, R.drawable.rect_11, R.string.title_location_11, R.string.subtitle_location_11, R.string.description_location_11,45.407062, 11.877064, false, R.drawable.marker_university),
            new PointOfInterest(11, R.drawable.round_12, R.drawable.rect_12, R.string.title_location_12, R.string.subtitle_location_12, R.string.description_location_12,45.407490, 11.875254, false, R.drawable.marker_phanteon),
            new PointOfInterest(12, R.drawable.round_13, R.drawable.rect_13, R.string.title_location_13, R.string.subtitle_location_13, R.string.description_location_13,45.408780, 11.875616, false, R.drawable.marker_church),
            new PointOfInterest(13, R.drawable.round_14, R.drawable.rect_14, R.string.title_location_14, R.string.subtitle_location_14, R.string.description_location_14,45.408791, 11.875082, false, R.drawable.marker_church),
            new PointOfInterest(14, R.drawable.round_15, R.drawable.rect_15, R.string.title_location_15, R.string.subtitle_location_15, R.string.description_location_15,45.407760, 11.873011, false, R.drawable.marker_tower),
            new PointOfInterest(15, R.drawable.round_16, R.drawable.rect_16, R.string.title_location_16, R.string.subtitle_location_16, R.string.description_location_16,45.408770, 11.872343, false, R.drawable.marker_church),
            new PointOfInterest(16, R.drawable.round_17, R.drawable.rect_17, R.string.title_location_17, R.string.subtitle_location_17, R.string.description_location_17,45.406687, 11.871948, false, R.drawable.marker_church),
            new PointOfInterest(17, R.drawable.round_18, R.drawable.rect_18, R.string.title_location_18, R.string.subtitle_location_18, R.string.description_location_18,45.407966, 11.872071, false, R.drawable.marker_phanteon),
            new PointOfInterest(18, R.drawable.round_19, R.drawable.rect_19, R.string.title_location_19, R.string.subtitle_location_19, R.string.description_location_19,45.408763, 11.868583, false, R.drawable.marker_church),
            new PointOfInterest(19, R.drawable.round_20, R.drawable.rect_20, R.string.title_location_20, R.string.subtitle_location_20, R.string.description_location_20,45.403242, 11.869298, false, R.drawable.marker_church),
            new PointOfInterest(20, R.drawable.round_21, R.drawable.rect_21, R.string.title_location_21, R.string.subtitle_location_21, R.string.description_location_21,45.401851, 11.868450, false, R.drawable.marker_tower)
            //new PointOfInterest(21, R.drawable.round_21, R.drawable.rect_21, R.string.title_location_21, R.string.subtitle_location_21, R.string.description_location_21,45.415175, 11.884655, false, R.drawable.marker_tower),
            //new PointOfInterest(22, R.drawable.round_21, R.drawable.rect_21, R.string.title_location_21, R.string.subtitle_location_21, R.string.description_location_21,45.412631, 11.886462, false, R.drawable.marker_tower),
            //new PointOfInterest(23, R.drawable.round_21, R.drawable.rect_21, R.string.title_location_21, R.string.subtitle_location_21, R.string.description_location_21,45.411601, 11.887051, false, R.drawable.marker_tower)

    };

    public POIsCreator() { }

    static public PointOfInterest[] getLocations(){
        return mPointOfInterests;
    }
}
