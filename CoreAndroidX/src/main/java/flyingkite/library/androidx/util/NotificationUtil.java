package flyingkite.library.androidx.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtil {

    private static final String CHANNEL_ID = "SMS Received";
    private static void createNotificationChannel(Context c) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID + "_name";//c.getString(R.string.channel_name);
            String description = CHANNEL_ID + "_desc";//getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            //c.getSystemService(NotificationManager.class);
            NotificationManagerCompat mgr = mgr(c);
            mgr.createNotificationChannel(channel);
        }
    }

    private static NotificationManagerCompat mgr(Context c) {
        return NotificationManagerCompat.from(c);
    }

    private void sendNotif(Context context, int iconID, String title, String msg) {
        createNotificationChannel(context);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconID)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                ;
        mgr(context).notify(156, b.build());
    }

    public static void sendNot(Context context, int iconID, String title, String msg) {
        createNotificationChannel(context);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconID)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                ;
        mgr(context).notify(157, b.build());
    }
}
