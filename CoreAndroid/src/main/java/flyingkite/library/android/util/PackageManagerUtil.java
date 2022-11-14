package flyingkite.library.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class PackageManagerUtil {
    private Context context;
    public PackageManager manager;
    // https://developer.android.com/training/data-storage/manage-all-files

    public PackageManagerUtil(Context c) {
        context = c;
        manager = context.getPackageManager();
    }

    // https://stackoverflow.com/questions/5661418/get-apk-file-icon-version-name
    public PackageInfo getPackageArchiveInfo(String apkPath) {
        PackageInfo pi = manager.getPackageArchiveInfo(apkPath, 0); // ~= 50ms, 77ms

        // the secret are these two lines....
        pi.applicationInfo.sourceDir = apkPath;
        pi.applicationInfo.publicSourceDir = apkPath;
        return pi;
    }

    public PackageInfo getPackageInfo(String packageName) {
        PackageInfo pi = null; // ~= 50ms, 77ms
        try {
            pi = manager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public Drawable getPackageIcon(String apkPath) {
        PackageInfo pi = getPackageArchiveInfo(apkPath);
        return pi.applicationInfo.loadIcon(manager); // ~25ms, 38ms
    }

    public CharSequence getPackageLabel(String apkPath) {
        PackageInfo pi = getPackageArchiveInfo(apkPath);
        return pi.applicationInfo.loadLabel(manager); // ~0ms, 0ms
    }
}
