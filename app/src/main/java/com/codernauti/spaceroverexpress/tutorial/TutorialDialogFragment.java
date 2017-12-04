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

package com.codernauti.spaceroverexpress.tutorial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codernauti.spaceroverexpress.R;
import com.codernauti.spaceroverexpress.utils.SharedPrefUtils;

public class TutorialDialogFragment extends BottomSheetDialog implements View.OnClickListener{

    private String assistantMsg;
    private String mSharedKeyTutorial;
    private int[] mIdMessages;
    private int mNumCurrentMessage = 0;

    private Context mContext;

    private TextView tutorialTextView;

    public TutorialDialogFragment(@NonNull Context context, String sharedKey, int[] messagesId, boolean cancelEnable) {
        super(context);

        View contentView = View.inflate(context, R.layout.info_marker_dialog, null);
        setContentView(contentView);

        mContext = context;
        mSharedKeyTutorial = sharedKey;
        mIdMessages = messagesId;

        contentView.findViewById(R.id.start_guide_btn)
                .setOnClickListener(this);

        Button cancelButton = (Button) contentView.findViewById(R.id.cancel_guide_btn);
        if(cancelEnable) cancelButton.setOnClickListener(this);
        else cancelButton.setVisibility(View.INVISIBLE);

        tutorialTextView = contentView.findViewById(R.id.info_location_selected);

        assistantMsg = mContext.getString(mIdMessages[mNumCurrentMessage]);
        tutorialTextView.setText(assistantMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_guide_btn: {
                if (mNumCurrentMessage < mIdMessages.length - 1) {
                    mNumCurrentMessage++;
                    assistantMsg = mContext.getString(mIdMessages[mNumCurrentMessage]);
                    tutorialTextView.setText(assistantMsg);
                } else {
                    SharedPrefUtils.saveBooleanPreference(mContext, mSharedKeyTutorial, true);
                    dismiss();
                }
                break;
            }
            case R.id.cancel_guide_btn: {
                dismiss();
                break;
            }
        }
    }

}