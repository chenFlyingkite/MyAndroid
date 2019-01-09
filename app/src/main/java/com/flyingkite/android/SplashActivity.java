package com.flyingkite.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class clazz;
        clazz = MainActivity.class;
        clazz = RecyclerActivity.class;
        //clazz = ModelViewActivity.class;

        startActivity(new Intent(this, clazz));
        finish();
    }
}
