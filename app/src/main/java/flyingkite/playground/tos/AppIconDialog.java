package flyingkite.playground.tos;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;

import flyingkite.playground.R;

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
            final int w = 512;
            String name = ShareHelper.cacheName(getActivity(), "w" + w + ".png");
            ShareHelper.shareImage(getActivity(), v, name, w, w);
        });

        findViewById(R.id.aicPlay1).setOnClickListener((v) -> {
            String name = ShareHelper.cacheName(getActivity(), "play.png");
            ShareHelper.shareImage(getActivity(), v, name, 1024, 500);
        });
    }
}
