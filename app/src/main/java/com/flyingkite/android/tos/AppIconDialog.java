package com.flyingkite.android.tos;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.flyingkite.android.R;

public class AppIconDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_app_icon;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adjust1();
    }

    private void adjust1() {
        findViewById(R.id.aicIcon1).setOnClickListener((v) -> {
            int s = 512;
            String name = ShareHelper.cacheName(getActivity(), s + ".png");
            ShareHelper.shareImage(getActivity(), v, name, s, s);
        });

        findViewById(R.id.aicPlay1).setOnClickListener((v) -> {
            String name = ShareHelper.cacheName(getActivity(), "play.png");
            ShareHelper.shareImage(getActivity(), v, name, 1024, 512);
        });
    }
}
