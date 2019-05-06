package com.flyingkite.android;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.flyingkite.library.log.Loggable;

import androidx.annotation.Nullable;

public abstract class BaseActivity extends Activity implements Loggable {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(%s)", savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log("onRestart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log("onRestoreInstanceState(%s)", savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        log("onSaveInstanceState(%s)", outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log("onConfigurationChanged(%s)", newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        log("onAttachedToWindow");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        log("onDetachedFromWindow");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    @Override
    public void log(String message) {
        logE(message);
    }
}
