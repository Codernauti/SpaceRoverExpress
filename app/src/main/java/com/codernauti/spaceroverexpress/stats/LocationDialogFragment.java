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

package com.codernauti.spaceroverexpress.stats;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.model.PointOfInterest;

public class LocationDialogFragment extends DialogFragment implements View.OnClickListener{

    private PointOfInterest mPointOfInterest;

    static LocationDialogFragment newInstance(PointOfInterest pointOfInterest) {
        LocationDialogFragment f = new LocationDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable("pointOfInterest", pointOfInterest);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       mPointOfInterest = getArguments().getParcelable("pointOfInterest");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_frag, container, false);
        ImageButton mCloseButton = v.findViewById(R.id.close_location_button);
        mCloseButton.setOnClickListener(this);

        TextView mTitleText = v.findViewById(R.id.location_title_text);
        TextView mSubtitleText = v.findViewById(R.id.location_subtitle_text);
        ImageView mCoverImage = v.findViewById(R.id.location_cover_image);
        TextView mDescriptionText = v.findViewById(R.id.location_description_text);

        mTitleText.setText(mPointOfInterest.getTitle());
        mSubtitleText.setText(mPointOfInterest.getSubtitle());
        mCoverImage.setImageResource(mPointOfInterest.getCoverImageResource());
        mDescriptionText.setText(mPointOfInterest.getDescription());

        return v;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.close_location_button: {
                    this.dismiss();
                break;
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
