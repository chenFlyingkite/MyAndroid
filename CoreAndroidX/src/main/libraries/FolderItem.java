package com.cyberlink.yousnap.libraries;

import android.os.Parcel;
import android.os.Parcelable;

public class FolderItem implements Parcelable {

    private final String mName;
    private final String mFullPath;
    private int mMediaCount;

    /**
     * Use to show as folder's cover.
     */
    private MediaItem mMediaItem;

    public FolderItem(String name, String fullPath, int count) {
        mName = name;
        mFullPath = fullPath;
        mMediaCount = count;
    }

    protected FolderItem(Parcel in) {
        mName = in.readString();
        mFullPath = in.readString();
        mMediaCount = in.readInt();
        mMediaItem = in.readParcelable(MediaItem.class.getClassLoader());
    }

    public static final Creator<FolderItem> CREATOR = new Creator<FolderItem>() {
        @Override
        public FolderItem createFromParcel(Parcel in) {
            return new FolderItem(in);
        }

        @Override
        public FolderItem[] newArray(int size) {
            return new FolderItem[size];
        }
    };

    public String getName() {
        return mName;
    }

    public String getFullPath() {
        return mFullPath;
    }

    public int getMediaCount() {
        return mMediaCount;
    }

    public void increaseMediaCount() {
        mMediaCount++;
    }

    public void addMediaCount(int count) {
        mMediaCount += count;
    }

    public MediaItem getRepresentedMediaItem() {
        return mMediaItem;
    }

    public void setRepresentedMediaItem(MediaItem mediaItem) {
        mMediaItem = mediaItem;
    }

    @Override
    public String toString() {
        return "[" + mMediaCount + "] " + mFullPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderItem folderItem = (FolderItem) o;

        return mFullPath.equals(folderItem.mFullPath);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mFullPath);
        dest.writeInt(mMediaCount);
        dest.writeParcelable(mMediaItem, 0);
    }
}
