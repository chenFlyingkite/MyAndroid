package com.flyingkite.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.flyingkite.core.log.Loggable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseActivity extends Activity implements Loggable {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(%s)", savedInstanceState);
        requestPermissions();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log("onActivityResult : %s -> %s, data = %s", requestCode, RESULT_STATE[resultCode + 1], data);
    }

    //-- Find fragment
    protected Fragment findFragmentById(@IdRes int fragmentId) {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return null;
        } else {
            return fm.findFragmentById(fragmentId);
        }
    }

    protected Fragment findFragmentByTag(String tag) {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return null;
        } else {
            return fm.findFragmentByTag(tag);
        }
    }
    //--

    protected static final int CODE_PERMISSION = 1;
    protected static final String[] RESULT_STATE = {"OK", "Cancel"};

    //-- Request Permission
    protected String[] neededPermissions() {
        return new String[0];
    }

    protected final void requestPermissions() {
        String[] permissions = neededPermissions();
        if (permissions == null || permissions.length == 0) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> perm = new ArrayList<>();
            perm.addAll(Arrays.asList(permissions));
            for (int i = perm.size() - 1; i >= 0; i--) {
                if (checkSelfPermission(perm.get(i)) == PackageManager.PERMISSION_GRANTED) {
                    perm.remove(i);
                }
            }

            if (perm.size() > 0) {
                requestPermissions(perm.toArray(new String[0]), CODE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_PERMISSION:
                log("Request permissions = %s", Arrays.toString(permissions));
                log("and returns results = %s", Arrays.toString(grantResults));
                break;
        }
    }
    //--

    @Override
    public void log(String message) {
        logE(message);
    }
}
