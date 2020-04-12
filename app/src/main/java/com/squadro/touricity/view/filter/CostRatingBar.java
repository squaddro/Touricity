package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squadro.touricity.R;

import lombok.Getter;

public class CostRatingBar {

    private RatingBar ratingBar = null;
    private TextView avgCostTextView = null;
    @Getter
    private int averageCost = 0;


    public CostRatingBar(Activity activity){
        initializeRatingBar(activity);

    }

    private void initializeRatingBar(Activity activity) {

        ratingBar = activity.findViewById(R.id.costRatingBar);
        avgCostTextView = activity.findViewById(R.id.avgCostValueTextView);
        ratingBar.setOnRatingBarChangeListener(getOnRatingBarChangeListener());
    }

    private RatingBar.OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return (ratingBar, rate, isFromUser) -> {
            averageCost = (int)rate;
            avgCostTextView.setText("" + rate);
        };
    }
}
