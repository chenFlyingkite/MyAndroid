package flyingkite.playground;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import flyingkite.library.android.log.Loggable;

public class App extends MultiDexApplication implements Loggable {
    public static App me;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        me = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    //----
    // https://android.googlesource.com/platform/frameworks/base.git/+/master/core/jni/android_media_AudioRecord.cpp


    private TextToSpeech ttsService;
    private void initTTS() {
        Context ctx = this;
        ttsService = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                log("Success = %s, Error = %s", TextToSpeech.SUCCESS, TextToSpeech.ERROR);
                log("onInit %s ok = %s", status, status == TextToSpeech.SUCCESS);

                speak("Hello, Welcome");
            }
        });
        ttsService.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                log("onStart : %s", utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                log("onDone : %s", utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                log("onError : %s", utteranceId);
            }
        });
    }

    public void speak(String text) {
        if (TextUtils.isEmpty(text)) return;

        log("says %s", text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsService.speak(text, TextToSpeech.QUEUE_ADD, null, text);
        } else {
            ttsService.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

}
