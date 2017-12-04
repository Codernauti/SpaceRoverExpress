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

import android.os.Parcel;
import android.os.Parcelable;

public class Badge implements Parcelable{

    private int mId;
    private String mKey;
    private int mTitleResource;
    private int mDescResource;
    private int mImageResource;

    Badge(int id, String key, int title, int descResource, int imagePath) {
        mId = id;
        mKey = key;
        mTitleResource = title;
        mDescResource = descResource;
        mImageResource = imagePath;
    }

    protected Badge(Parcel in) {
        mId = in.readInt();
        mKey = in.readString();
        mTitleResource = in.readInt();
        mDescResource = in.readInt();
        mImageResource = in.readInt();
    }

    public static final Creator<Badge> CREATOR = new Creator<Badge>() {
        @Override
        public Badge createFromParcel(Parcel in) {
            return new Badge(in);
        }

        @Override
        public Badge[] newArray(int size) {
            return new Badge[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public int getTitle(){
        return mTitleResource;
    }

    public int getDescription(){
        return mDescResource;
    }

    public int getImageResource() {
        return mImageResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mKey);
        parcel.writeInt(mTitleResource);
        parcel.writeInt(mDescResource);
        parcel.writeInt(mImageResource);
    }

}
