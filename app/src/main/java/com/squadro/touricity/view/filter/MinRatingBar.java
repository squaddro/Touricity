package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.widget.RatingBar;

import com.squadro.touricity.R;

public class MinRatingBar {

    private RatingBar ratingBar = null;
    private double minRate = 0.0;

    public MinRatingBar(Activity activity){
        initializeRatingBar(activity);

    }

    private void initializeRatingBar(Activity activity) {

        ratingBar = activity.findViewById(R.id.minRatingBar);
        ratingBar.setOnRatingBarChangeListener(getOnRatingBarChangeListener());
    }

    private RatingBar.OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return (ratingBar, rate, isFromUser) -> {
            minRate = rate;
        };
    }
}
