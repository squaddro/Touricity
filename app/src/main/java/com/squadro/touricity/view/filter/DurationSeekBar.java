package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squadro.touricity.R;

public class DurationSeekBar {

    private int duration = 0;
    private SeekBar seekBar = null;
    private TextView durationTextView = null;

    public DurationSeekBar(Activity activity){
        initializeSeekBar(activity);
    }

    private void initializeSeekBar(Activity activity) {
        seekBar = activity.findViewById(R.id.durationSeekBar);
        durationTextView = activity.findViewById(R.id.durationValueTextView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                duration = i;
                durationTextView.setText("" + duration);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
