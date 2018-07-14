package com.flyingkite.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.flyingkite.library.logging.Loggable;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class clazz;
        clazz = MainActivity.class;
        //clazz = RecyclerActivity.class;

        startActivity(new Intent(this, clazz));
        finish();
    }

    private static Loggable x = new Loggable() {};
    private static void f() {
        x.log("123");
    }
}
