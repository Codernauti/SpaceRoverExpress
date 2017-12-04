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

import android.os.Parcel;
import android.os.Parcelable;

public class PointOfInterest implements Parcelable{

    private int mId;
    private int mPreviewImageResource;
    private int mCoverImageResource;
    private int mTitleResource;
    private int mSubtitleResource;
    private int mDescriptionResource;
    private double mLatitude;
    private double mLongitude;
    private boolean mFound;
    private int mIconMarkerResource;


    PointOfInterest(int id,
                    int previewImageResourceId,
                    int coverImageResourceId,
                    int  title,
                    int subtitle,
                    int description,
                    double latitude,
                    double longitude,
                    boolean found,
                    int icorMarkerResourceId) {
        mId = id;
        mPreviewImageResource = previewImageResourceId;
        mCoverImageResource = coverImageResourceId;
        mTitleResource = title;
        mSubtitleResource = subtitle;
        mDescriptionResource = description;
        mLatitude = latitude;
        mLongitude = longitude;
        mFound = found;
        mIconMarkerResource = icorMarkerResourceId;
    }

    protected PointOfInterest(Parcel in) {
        mId = in.readInt();
        mPreviewImageResource = in.readInt();
        mCoverImageResource = in.readInt();
        mTitleResource = in.readInt();
        mSubtitleResource = in.readInt();
        mDescriptionResource = in.readInt();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mFound = in.readInt() == 1;
        mIconMarkerResource = in.readInt();
    }

    public static final Creator<PointOfInterest> CREATOR = new Creator<PointOfInterest>() {
        @Override
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        @Override
        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };

    public int getId() {
        return mId;
    }

    public int getPreviewImageResource() {
        return mPreviewImageResource;
    }

    public int getCoverImageResource(){
        return mCoverImageResource;
    }

    public int getTitle() {
        return mTitleResource;
    }

    public int getSubtitle(){
        return mSubtitleResource;
    }

    public int getDescription() {
        return mDescriptionResource;
    }

    public Double getLatitude() { return mLatitude; }

    public Double getLongitude() { return mLongitude; }

    public boolean isFound(){
        return mFound;
    }

    public void setFound(boolean isFound){
        mFound = isFound;
    }

    public int getIconMarkerResource() {
        return mIconMarkerResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeInt(mPreviewImageResource);
        parcel.writeInt(mCoverImageResource);
        parcel.writeInt(mTitleResource);
        parcel.writeInt(mSubtitleResource);
        parcel.writeInt(mDescriptionResource);
        parcel.writeDouble(mLatitude);
        parcel.writeDouble(mLongitude);
        parcel.writeInt(mFound ? 1 : 0);
        parcel.writeInt(mIconMarkerResource);
    }

}
