package com.cyberlink.yousnap.libraries;

import android.provider.MediaStore;

public enum OrderBy {
    PATH(MediaStore.MediaColumns.DATA, ASC()),
    /**
     * For images and videos, it is roughly the file name without extension.
     */
    TITLE(MediaStore.MediaColumns.TITLE, ASC()),
    NAME(MediaStore.MediaColumns.DISPLAY_NAME + " COLLATE NOCASE", ASC()), // Use "COLLATE NOCASE" to be case-insensitive
    DATE_TAKEN(MediaStore.Images.ImageColumns.DATE_TAKEN, DESC()),
    DURATION(MediaStore.Video.VideoColumns.DURATION, DESC()),
    RESOLUTION(MediaStore.MediaColumns.WIDTH, DESC()),
    SIZE(MediaStore.MediaColumns.SIZE, DESC()),
    DATE_MODIFIED(MediaStore.Images.Media.DATE_MODIFIED, DESC());


    public final String column;
    public final String order; //ASC or DESC

    OrderBy(String col, String ord) {
        column = col;
        order = ord;
    }

    private static String ASC() {
        return "ASC";
    }
    private static String DESC() {
        return "DESC";
    }

    @Override
    public String toString() {
        return column + " " + order;
    }
}