package com.flyingkite.android;

import android.app.Activity;
import android.content.Intent;
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
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;

import com.flyingkite.android.tos.AppIconDialog;
import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private MediaProjectionManager mpm;
    private MediaProjection mpj;
    private DisplayMetrics metrics = new DisplayMetrics();
    private SurfaceView surfView;
    private VirtualDisplay display;
    private static final int REQ_SCREENSHOT = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTos();

        mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Say.Log("metrics = %s", metrics);
        surfView = findViewById(R.id.screenSurface);

        findViewById(R.id.screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendKeyEvent();
            }

            private void screenShot() {
                if (mpm == null) return;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Say.Log("Screen");
                    startActivityForResult(mpm.createScreenCaptureIntent(), REQ_SCREENSHOT);
                }
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
                Say.Log("Screen %s", cmd);
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
                Say.Log("%s", cmd);
            }
        });
        findViewById(R.id.screenSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Say.Log("Save");
                saveImage(surfView);
            }
        });
    }

    private void setTos() {
        findViewById(R.id.tosApp).setOnClickListener((v) -> {
            new AppIconDialog().show(MainActivity.this);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Say.Log("onBackPressed");
    }

    private ImageReader reader;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Say.Log("onActivityResult(%s, %s, %s)", requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                            Say.Log("VD : onPaused");
                        }

                        @Override
                        public void onResumed() {
                            super.onResumed();
                            Say.Log("VD : onResumed");
                        }

                        @Override
                        public void onStopped() {
                            super.onStopped();
                            Say.Log("VD : onStopped");
                        }
                    }, null);
            Say.Log("+ : display = %s", display);
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
        Say.Log("save as %s, bmp = %s x %s, %s dpi", f, bmp.getWidth(), bmp.getHeight(), bmp.getDensity());
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
