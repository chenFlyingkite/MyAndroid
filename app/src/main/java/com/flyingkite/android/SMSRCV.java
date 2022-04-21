package com.flyingkite.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.flyingkite.library.log.Loggable;

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
            for (int i = 0; i < n; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    msgs[i] = SmsMessage.createFromPdu(pdus, b.getString("format"));
                } else {
                    msgs[i] = SmsMessage.createFromPdu(pdus);
                }
                logE("#%s : %s", i, msgs[i]);
            }
        }
    }
}
