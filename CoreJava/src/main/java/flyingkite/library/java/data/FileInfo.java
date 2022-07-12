package flyingkite.library.java.data;

import com.google.gson.annotations.SerializedName;

public class FileInfo {
    @SerializedName("fileCount")
    public int fileCount;

    @SerializedName("fileSize")
    public long fileSize;

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileCount=" + fileCount +
                ", fileSize=" + fileSize +
                '}';
    }
}
