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
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.game.GameActivity;
import com.codernauti.spaceroverexpress.game.GameEndActivity;
import com.codernauti.spaceroverexpress.model.PointOfInterest;

public class DiscoveredPoiDialog extends BottomSheetDialog implements View.OnClickListener {

    private final PointOfInterest mPoi;

    public DiscoveredPoiDialog(@NonNull Context context, PointOfInterest poi) {
        super(context);

        mPoi = poi;

        View contentView = View.inflate(context, R.layout.discoverd_poi_dialog, null);
        setContentView(contentView);

        /*contentView.findViewById(R.id.cancel_game_start_btn)
                .setOnClickListener(this);*/
        contentView.findViewById(R.id.start_game_btn)
                .setOnClickListener(this);

        TextView textView = contentView.findViewById(R.id.tutorial_text);
        textView.setText(getContext().getString(R.string.trigger_location_found_dialog) + " " + getContext().getString(poi.getTitle()) + "!");

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) contentView.getParent());

        if (behavior != null) {
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_game_btn: {
                Intent intent = new Intent(getContext(), GameActivity.class);
                getContext().startActivity(intent);
                dismiss();
                break;
            }
            /*case R.id.cancel_game_start_btn: {
                dismiss();
                break;
            }*/
        }
    }
}
