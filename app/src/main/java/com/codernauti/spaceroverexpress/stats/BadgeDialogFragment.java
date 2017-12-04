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

public class BadgeDialogFragment extends DialogFragment implements View.OnClickListener{

    Badge mBadge;

    static BadgeDialogFragment newInstance(Badge badge) {
        BadgeDialogFragment f = new BadgeDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable("badge", badge);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBadge = getArguments().getParcelable("badge");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.badge_frag, container, false);

        ImageButton mCloseButton = v.findViewById(R.id.close_badge_button);
        mCloseButton.setOnClickListener(this);

        TextView mTitleText = v.findViewById(R.id.badge_title_text);
        TextView mDescriptionText = v.findViewById(R.id.badge_description_text);
        ImageView mCoverImage = v.findViewById(R.id.badge_image);

        mTitleText.setText(mBadge.getTitle());
        mDescriptionText.setText(mBadge.getDescription());
        mCoverImage.setImageResource(mBadge.getImageResource());


        return v;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.close_badge_button: {
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

