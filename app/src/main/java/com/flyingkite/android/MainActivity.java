package com.flyingkite.android;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyingkite.android.tos.AppIconDialog;
import com.flyingkite.core.log.Loggable;
import com.flyingkite.core.util.IOUtil;
import com.flyingkite.library.mediastore.MediaStoreKit;
import com.flyingkite.library.mediastore.listener.DataListener;
import com.flyingkite.library.mediastore.request.MediaRequest;
import com.flyingkite.library.recyclerview.Library;
import com.flyingkite.library.widget.ViewDisplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity implements Loggable {

    private MediaProjectionManager mpm;
    private MediaProjection mpj;
    private DisplayMetrics metrics = new DisplayMetrics();
    private SurfaceView surfView;
    private VirtualDisplay display;
    private static final int REQ_SCREENSHOT = 123;

    // Views
    private View screen;
    private View testFade;
    private final int SMSREQ = 12034;
    private TextView textInfo;

    private ViewDisplayer screenVD;
    private ViewDisplayer fadeVD;
    private int now = 0;

    private Library<TextAdapter> randomsRecycler;
    private TextAdapter textAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTos();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        logE("metrics = %s", metrics);
        surfView = findViewById(R.id.screenSurface);
        screen = findViewById(R.id.screen);

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendKeyEvent();
            }

        });
        findViewById(R.id.screenSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logE("Save");
                saveImage(surfView);
            }
        });

        screenVD = new ViewDisplayer(screen);
        initSMS();
        initRandom();
        initAnimation();
    }

    private void initRandom() {
        EditText rmax = findViewById(R.id.randomMax);
        findViewById(R.id.doRandom).setOnClickListener((v) -> {
            List<String> ans = new ArrayList<>();
            Random r = new Random();
            int max = 1;
            try {
                int dx = Integer.parseInt(rmax.getText().toString());
                max = Math.max(max, dx);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 100; i++) {
                int x = r.nextInt(max);
                String s = String.format("#%s: %s", i, x);
                ans.add(s);
            }
            textAdapter.setDataList(ans);
            textAdapter.notifyDataSetChanged();
        });
        randomsRecycler = new Library<>(findViewById(R.id.randoms), true);
        textAdapter = new TextAdapter();
        randomsRecycler.setViewAdapter(textAdapter);
    }

    private void initSMS() {
        View screen2 = findViewById(R.id.screen2);
        fadeVD = new ViewDisplayer(screen2) {
            final int dx = 200;
            final int dy = 100;
            @Override
            public void onShow(View v) {
                //super.onShow(v);
                if (v != null) {
                    v.clearAnimation();
                    v.animate().xBy(+dx).yBy(+dy).setListener(onShow).start();
                }
            }

            @Override
            public void onHide(View v) {
                //super.onHide(v);
                if (v != null) {
                    v.clearAnimation();
                    v.animate().xBy(-dx).yBy(-dy).setListener(onHide).start();
                }
            }
        };

        screen2.setOnClickListener((v) -> {
            if (askSMS()) {
                readSMS();
            }
        });

        findViewById(R.id.askSMS).setOnClickListener((v) -> {
            askSMS();
        });
        findViewById(R.id.readSMS).setOnClickListener((v) -> {
            readSMS();
        });
        findViewById(R.id.smsGet).setOnClickListener((v) -> {
            BroadcastReceiver sms = new SMSRCV();
            IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            Intent ret = registerReceiver(sms, filter);
            logE("ret = %s", ret);
            SMSRCV.sendNot(this, "hello", new Date().toString());
        });
        textInfo = findViewById(R.id.textInfo);
    }

    private void initAnimation() {
        testFade = findViewById(R.id.testFade);
        testFade.setOnClickListener((v) -> {
            logE("now = %s", now);
            screenVD.performAction(now);
            fadeVD.performAction(now);
            final int max = ViewDisplayer.ACTION_SHOW_THEN_HIDE_WHEN_IDLE + 1;
            now = (now + 1) % max;
        });
    }

    private boolean askSMS() {
        final String sms = Manifest.permission.READ_SMS;
        if (ContextCompat.checkSelfPermission(this, sms) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{sms}, SMSREQ);
        }
        return false;
    }

    private void readSMS() {
        logE("Read SMS");
        MediaStoreKit kit = new MediaStoreKit(this);
        MediaRequest sms = new MediaRequest();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sms.uri = Telephony.Sms.CONTENT_URI;
        }
        sms.listener = new DataListener<Map<String, String>>() {
            @Override
            public void onPreExecute() {
                logE("preExe");
            }

            @Override
            public void onQueried(int count, Cursor cursor) {
                logE("found %s SMS in %s", count, cursor);
            }

            @Override
            public void onProgress(int position, int count, Map<String, String> data) {
                logE("#%s/%s : %s", position, count, data);
            }

            @Override
            public void onComplete(List<Map<String, String>> all) {
                logE("onComplete %s", all.size());
            }

            @Override
            public void onError(Exception error) {
                logE("onError %s", error);
            }
        };
        kit.queryRequest(sms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        logE("onRequestPermissionsResult %s, %s, %s", requestCode, Arrays.toString(permissions), Arrays.toString(grantResults));
        if (requestCode == SMSREQ) {
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSMS();
            }
        }
    }

    private void setTos() {
        findViewById(R.id.tosApp).setOnClickListener((v) -> {
            new AppIconDialog().show(MainActivity.this);
            //new MediaStoreTester(getApplicationContext()).test();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logE("onBackPressed");
    }

    private boolean isAtLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private void screenShot() {
        if (!isAtLeastLollipop()) return;

        if (mpm == null) {
            mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        }
        if (mpm == null) return;

        logE("Screen");
        startActivityForResult(mpm.createScreenCaptureIntent(), REQ_SCREENSHOT);
    }

    private void sendKeyEvent() {
        // https://android.googlesource.com/platform/frameworks/base/+/HEAD/cmds/input/src/com/android/commands/input/Input.java
        int code;
        //code = KeyEvent.KEYCODE_BACK;
        //code = KeyEvent.KEYCODE_HOME;
        //code = KeyEvent.KEYCODE_CAMERA;
        //code = KeyEvent.KEYCODE_POWER;
        code = KeyEvent.KEYCODE_VOLUME_UP;
        //code = KeyEvent.KEYCODE_APP_SWITCH;
        String cmd = String.format("input keyevent %s", code);
        logE("Screen %s", cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        View v = findViewById(R.id.screenSave);
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        cmd = String.format("input tap %s %s", xy[0] + v.getWidth() / 2, xy[1] + v.getHeight() / 2);
        //cmd = "input swipe 100 500 200 1450 500";
        //90 + 180 * k
        cmd = "input swipe 90 900 90 1700 500";
        try {
            //Say.sleep(5000);
            Process process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logE("%s", cmd);
    }

    private ImageReader reader;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logE("onActivityResult(%s, %s, %s)", requestCode, resultCode, data);
        if (isAtLeastLollipop() && mpm != null) {
            // setUpMediaProjection
            mpj = mpm.getMediaProjection(resultCode, data);
            // setUpVirtualDisplay
            reader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels,
                    PixelFormat.RGBA_8888, 1);
            display = mpj.createVirtualDisplay("MyScreen",
                    metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    reader.getSurface()
                    //surfView.getHolder().getSurface()
                    , new VirtualDisplay.Callback() {
                        @Override
                        public void onPaused() {
                            super.onPaused();
                            logE("VD : onPaused");
                        }

                        @Override
                        public void onResumed() {
                            super.onResumed();
                            logE("VD : onResumed");
                        }

                        @Override
                        public void onStopped() {
                            super.onStopped();
                            logE("VD : onStopped");
                        }
                    }, null);
            logE("+ : display = %s", display);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mpj != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mpj.stop();
//            }
//            mpj = null;
//        }
    }

    private void saveImage(View view) {
        if (reader == null) return;

        boolean oldDrawCache = view.isDrawingCacheEnabled();
        view.setDrawingCacheEnabled(true);
        Bitmap b = view.getDrawingCache();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // https://www.jianshu.com/p/d7eb518195fd
            Image image = reader.acquireLatestImage();
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            //Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            // Erase side
            for (int i = width; i < width + rowPadding / pixelStride; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, Color.RED);
                }
            }
            //bitmap.setWidth(width);

            image.close();
            saveImage(bitmap);
        }

        //saveImage(b);
    }

    protected void saveImage(Bitmap bmp) {
        String timePng = String.format("%s.png",
                new SimpleDateFormat("HHmmss").format(new Date()));
        File f = new File(getExternalCacheDir(), timePng);
        if (f.exists()) {
            f.delete();
        }
        logE("save as %s, bmp = %s x %s, %s dpi", f, bmp.getWidth(), bmp.getHeight(), bmp.getDensity());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fos);
        }

    }

    private void setUpVirtualDisplay() {
//        Log.i(TAG, "Setting up a VirtualDisplay: " +
//                mSurfaceView.getWidth() + "x" + mSurfaceView.getHeight() +
//                " (" + mScreenDensity + ")");
//        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
//                mSurfaceView.getWidth(), mSurfaceView.getHeight(), mScreenDensity,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                mSurface, null, null);
//        mButtonToggle.setText(R.string.stop);
    }
}
