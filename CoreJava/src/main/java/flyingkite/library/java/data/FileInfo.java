package flyingkite.library.java.data;

import com.google.gson.annotations.SerializedName;

public class FileInfo {
    @SerializedName("fileCount")
    public int fileCount;

    @SerializedName("folderCount")
    public int folderCount;

    @SerializedName("fileSize")
    public long fileSize;

    @SerializedName("lastModified")
    public long lastModified;

    @Override
    public String toString() {
        return String.format("%s files, %s folders, %s bytes, last modify = %s", fileCount, folderCount, fileSize, lastModified);
    }
}
