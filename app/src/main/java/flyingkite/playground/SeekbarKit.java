package flyingkite.playground;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekbarKit implements SeekBar.OnSeekBarChangeListener {
    public SeekBar seekBar;
    public TextView textView;

    public SeekbarKit(View v) {
        if (v != null) {
            seekBar = v.findViewById(R.id.itemSeekBar);
            textView = v.findViewById(R.id.itemSeekText);
        }
        updateListener();
    }

    public String getTextDisplay(SeekbarKit me) {
        return me.seekBar.getProgress() + "";
    }

    public void updateListener() {
        if (seekBar != null && textView != null) {
            seekBar.setOnSeekBarChangeListener(this);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textView.setText(getTextDisplay(SeekbarKit.this));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
