package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squadro.touricity.R;

public class AverageCostSeekBar {

    private int averageCost = 0;
    private SeekBar seekBar = null;
    private TextView avgCostTextView = null;

    public AverageCostSeekBar(Activity activity){
        initializeSeekBar(activity);
    }

    private void initializeSeekBar(Activity activity) {
        seekBar = activity.findViewById(R.id.avgCostSeekBar);
        avgCostTextView = activity.findViewById(R.id.avgCostValueTextView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                averageCost = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                avgCostTextView.setText("" + averageCost);
            }
        });
    }

}
