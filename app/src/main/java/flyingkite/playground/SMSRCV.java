package flyingkite.playground;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import flyingkite.library.android.log.Loggable;
import com.flyingkite.playground.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

// untested
@Deprecated
public class SMSRCV extends BroadcastReceiver implements Loggable {
    @Override
    public void onReceive(Context context, Intent intent) {
        logE("context = %s, intent = %s", context, intent);
        Bundle b = intent.getExtras();
        logE("bundle = %s", b);
        if (b != null) {
            byte[] pdus = b.getByteArray("pdus");
            int n = pdus.length;
            final SmsMessage[] msgs = new SmsMessage[n];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    msgs[i] = SmsMessage.createFromPdu(pdus, b.getString("format"));
                } else {
                    msgs[i] = SmsMessage.createFromPdu(pdus);
                }
                logE("#%s : %s", i, msgs[i]);
                sb.append(msgs[i].getMessageBody());
            }
            String title = "SMSRCV";
            if (n > 0) {
                title = msgs[0].getEmailFrom();
            }
            sendNotif(context, title, sb.toString());
        }
    }

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

    private void sendNotif(Context context, String title, String msg) {
        createNotificationChannel(context);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                ;
        mgr(context).notify(156, b.build());
    }

    public static void sendNot(Context context, String title, String msg) {
        createNotificationChannel(context);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                ;
        mgr(context).notify(157, b.build());
    }
}
