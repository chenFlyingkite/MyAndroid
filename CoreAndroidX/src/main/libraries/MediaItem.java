package com.cyberlink.yousnap.libraries;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.IOException;

public class MediaItem implements Parcelable {

    public enum MediaType {
        IMAGE//, VIDEO, AUDIO
    }

    private static final Uri URI_IMAGE_MEDIA = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//    private static final Uri URI_VIDEO_MEDIA = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//    private static final Uri URI_AUDIO_MEDIA = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//    private static final Uri URI_AUDIO_ALBUM_ART = Uri.parse("content://media/external/audio/albumart");

    public static MediaItem createImage(long id, String filePath, int orientation, long dateTaken) {
        return createImage(id, filePath, null, null, 0, 0, 0, orientation, dateTaken, null);
    }

    public static MediaItem createImage(long id, String filePath,
                                        String displayName, String mimeType, int width, int height, long size, int orientation,
                                        long dateTaken, String bucketName) {
        return new MediaItem(MediaType.IMAGE, id, filePath, displayName, mimeType, width, height, size, orientation, dateTaken, bucketName);
    }

    /*
    public static MediaItem createVideo(long id, String filePath, int orientation, long dateTaken) {
        return createVideo(id, filePath, null, null, 0, 0, 0, 0, orientation, dateTaken, null);
    }

    public static MediaItem createVideo(long id, String filePath,
                                        String displayName, String mimeType, int width, int height, long size, long duration, int orientation,
                                        long dateTaken, String bucketName) {
        return new MediaItem(MediaType.VIDEO, id, filePath, displayName, mimeType, width, height, size, duration, orientation, dateTaken, bucketName);
    }

    public static MediaItem createAudio(long id, String filePath) {
        return createAudio(id, filePath, null, null, 0, 0, 0, null);
    }

    public static MediaItem createAudio(long id, String filePath,
                                        String displayName, String mimeType, long size, long duration, long albumId, String artist) {
        return new MediaItem(MediaType.AUDIO, id, filePath, displayName, mimeType, size, duration, albumId, artist);
    }
    */

    private final MediaType mType;

    /** The id stored in MediaStore. */
    private final long mId;
    private final String mFilePath;
    private final String mDisplayName;
    private final String mMimeType;

    /** The disk size of the media in bytes. */
    private final long mSize;
    /** Taken date for video or image. */
    private final long mDateTaken;

    /** Width for video or image. */
    private int mWidth;
    /** Height for video or image. */
    private int mHeight;
    /** Orientation for video or image */
    private int mOrientation;
    /** Duration in microseconds (us) for Video or Audio. */
    //private long mDuration;
    /** The parent folder name. */
    private final String mBucketName;

    // Only used for AUDIO type.
    /** AlbumId in MediaStore. */
    //private final long mAlbumId;
    /** Artist of the music */
    //private final String mArtist;

    private MediaItem(MediaType type, long id, String filePath, String displayName
            , String mimeType, long size, long dateTaken, int width, int height
            , int orientation, String bucketName
            //, long duration, long albumId, String artist
    ) {
        this.mType = type;
        this.mId = id;
        this.mFilePath = filePath;
        this.mDisplayName = displayName;
        this.mMimeType = mimeType;
        this.mSize = size;
        this.mDateTaken = dateTaken;
        this.mWidth = width;
        this.mHeight = height;
        this.mOrientation = orientation;
        //this.mDuration = duration;
        this.mBucketName = bucketName;
        //this.mAlbumId = albumId;
        //this.mArtist = artist;
    }

    public MediaItem copy() {
        return new MediaItem(mType, mId, mFilePath, mDisplayName,
                mMimeType, mSize, mDateTaken, mWidth, mHeight,
                mOrientation, mBucketName
                //, mDuration, mAlbumId, mArtist
        );
    }

    /**
     * Construct a Video type MediaItem.
     */
    /*
    private MediaItem(MediaType type, long id, String filePath, String displayName, String mimeType, int width, int height, long size, long duration, int orientation, long dateTaken, String bucketName) {
        this(type, id, filePath, displayName, mimeType, size, dateTaken, width, height, orientation, duration, bucketName, 0, null);
    }
    */

    /**
     * Construct a IMAGE type MediaItem.
     */
    private MediaItem(MediaType type, long id, String filePath, String displayName, String mimeType, int width, int height, long size, int orientation, long dateTaken, String bucketName) {
        this(type, id, filePath, displayName, mimeType, size, dateTaken, width, height, orientation, bucketName
                //, 0, 0, null
        );
    }

    /**
     * Construct a AUDIO type MediaItem.
     */
    /*
    private MediaItem(MediaType type, long id, String filePath, String displayName, String mimeType, long size, long duration, long albumId, String artist) {
        this(type, id, filePath, displayName, mimeType, size, 0, 0, 0, 0, duration, null, albumId, artist);
    }
    */

    protected MediaItem(Parcel in) {
        mId = in.readLong();
        mFilePath = in.readString();
        mDisplayName = in.readString();
        mMimeType = in.readString();
        mHeight = in.readInt();
        mWidth = in.readInt();
        mSize = in.readLong();
        //mDuration = in.readLong();
        mOrientation = in.readInt();
        mBucketName = in.readString();
        //mAlbumId = in.readLong();
        //mArtist = in.readString();
        mDateTaken = in.readLong();
        mType = MediaType.values()[in.readInt()];
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mFilePath);
        dest.writeString(mDisplayName);
        dest.writeString(mMimeType);
        dest.writeInt(mHeight);
        dest.writeInt(mWidth);
        dest.writeLong(mSize);
        //dest.writeLong(mDuration);
        dest.writeInt(mOrientation);
        dest.writeString(mBucketName);
        //dest.writeLong(mAlbumId);
        //dest.writeString(mArtist);
        dest.writeLong(mDateTaken);
        dest.writeInt(mType.ordinal());
    }

    public long getDateTaken() {
        return mDateTaken;
    }

    public long getId() {
        return mId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public long getSize() {
        return mSize;
    }

    public int getWidth() {
        return mWidth;
    }

    /**
     * @deprecated It's a lazy implementation, should replace adapter item but not update one of its final attributes.
     */
    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * @deprecated It's a lazy implementation, should replace adapter item but not update one of its final attributes.
     */
    public void setHeight(int height) {
        this.mHeight = height;
    }

    public int getOrientation() {
        return mOrientation;
    }

    /**
     * @deprecated It's a lazy implementation, should replace adapter item but not update one of its final attributes.
     */
    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public Uri getUri() {
        Uri uri = null;
        if (mId >= 0) {
            if (mType == MediaType.IMAGE)
                uri = Uri.withAppendedPath(URI_IMAGE_MEDIA, String.valueOf(mId));
//            else if (mType == MediaType.VIDEO)
//                uri = Uri.withAppendedPath(URI_VIDEO_MEDIA, String.valueOf(mId));
//            else if (mType == MediaType.AUDIO)
//                uri = Uri.withAppendedPath(URI_AUDIO_MEDIA, String.valueOf(mId));
        }
        return uri;
    }

    /*
    public Uri getAlbumartUri() {
        Uri uri = null;
        if(mType == MediaType.AUDIO)
            uri = Uri.withAppendedPath(URI_AUDIO_ALBUM_ART, String.valueOf(mAlbumId));

        return uri;
    }
    */

    public MediaType getType() {
        return mType;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getCaptureTime() {
        ExifInterface exif;
        String date = null;
        try {
            exif = new ExifInterface(mFilePath);
            date = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return date;
    }

    public boolean isPortrait() {
        return (mOrientation == 270 || mOrientation == 90) ? mWidth > mHeight : mHeight > mWidth;
    }

    /*
    public long getDuration() {
        return mDuration;
    }
    */

    /**
     * @deprecated It's a lazy implementation, should replace adapter item but not update one of its final attributes.
     */
    /*
    public void setDuration(long duration) {
        this.mDuration = duration;
    }
    */

    /*
    public String getArtist() {
        return mArtist;
    }
    */

    @Override
    public String toString() {
        return mDisplayName;
//        if (mType == MediaType.AUDIO) {
//            return mDisplayName + ", " + mSize + ", " + mFilePath + ", " + mDuration + ", " + mAlbumId;
//        } else {
//            return mDisplayName;
//        }
    }
}
