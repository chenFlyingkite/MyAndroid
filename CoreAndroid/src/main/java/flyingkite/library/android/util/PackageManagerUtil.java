package flyingkite.library.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class PackageManagerUtil {
    private Context context;
    public PackageManager manager;

    public PackageManagerUtil(Context c) {
        context = c;
        manager = context.getPackageManager();
    }

    private PackageManager manager() {
        return context.getPackageManager();
    }

    // https://stackoverflow.com/questions/5661418/get-apk-file-icon-version-name
    public PackageInfo getPackageInfo(String apkPath) {
        PackageInfo pi = manager.getPackageArchiveInfo(apkPath, 0); // ~= 50ms, 77ms

        // the secret are these two lines....
        pi.applicationInfo.sourceDir = apkPath;
        pi.applicationInfo.publicSourceDir = apkPath;
        return pi;
    }

    public Drawable getPackageIcon(String apkPath) {
        PackageInfo pi = getPackageInfo(apkPath);
        return pi.applicationInfo.loadIcon(manager); // ~25ms, 38ms
    }

    public CharSequence getPackageLabel(String apkPath) {
        PackageInfo pi = getPackageInfo(apkPath);
        return pi.applicationInfo.loadLabel(manager); // ~0ms, 0ms
    }
}
