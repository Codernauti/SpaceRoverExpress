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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.PointOfInterest;

public class InfoMarkerPoiDialog extends BottomSheetDialog implements View.OnClickListener{

    private final PointOfInterest mPoi;

    private Listener mListener;

    interface Listener {
        void onGuideClicked();
    }

    public void setListener(Listener listener){
         mListener = listener;
    }


    public InfoMarkerPoiDialog(@NonNull Context context, PointOfInterest poi) {
        super(context);

        mPoi = poi;

        View contentView = View.inflate(context, R.layout.info_marker_dialog, null);
        setContentView(contentView);

        contentView.findViewById(R.id.start_guide_btn)
                .setOnClickListener(this);

        contentView.findViewById(R.id.cancel_guide_btn)
                .setOnClickListener(this);


        TextView textView = contentView.findViewById(R.id.info_location_selected);
        String assistantMsg =
                context.getString(R.string.new_destination_set_partI) + "\"" +
                context.getString(poi.getTitle()) + "\"" +
                context.getString(R.string.new_destination_set_partII);

        textView.setText(assistantMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_guide_btn: {
                mListener.onGuideClicked();
                dismiss();
                break;
            }
            case R.id.cancel_guide_btn: {
                dismiss();
                break;
            }
        }
    }

}
