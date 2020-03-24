package com.squadro.touricity.view.map.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.view.map.MyPlace;
import com.squadro.touricity.view.routeList.entry.StopCardView;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class StopCardViewHandler {

    private final Context context;
    private StopCardView stopCardView;
    private MyPlace myPlace;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public StopCardViewHandler(StopCardView stopCardView, MyPlace myPlace, Context context) {
        this.stopCardView = stopCardView;
        this.myPlace = myPlace;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public StopCardView putViews() {
        LinearLayout linearLayoutVertical = new LinearLayout(context);
        linearLayoutVertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutVertical.setOrientation(VERTICAL);
        linearLayoutVertical.setPadding(10, 5, 10, 5);

        LinearLayout linearLayoutHorizontal = new LinearLayout(context);
        linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutHorizontal.setOrientation(HORIZONTAL);

        LinearLayout textAreasLayout = stopCardView.findViewById(R.id.textAreas);
        if (myPlace.getPhotos() != null && !myPlace.getPhotos().isEmpty()) {
            for (Bitmap bm : myPlace.getPhotos()) {
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(1024, 720));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageBitmap(bm);
                imageView.setPadding(10, 0, 10, 0);
                LinearLayout viewById = stopCardView.findViewById(R.id.img_layout);
                viewById.addView(imageView);
            }
        } if (myPlace.getName() != null && !myPlace.getName().isEmpty()) {
            TextView nameTextView = new TextView(context);
            nameTextView.setTextSize(20);
            nameTextView.setTypeface(null, Typeface.BOLD);
            nameTextView.setElegantTextHeight(true);
            nameTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            nameTextView.setSingleLine(false);
            nameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            nameTextView.setText(myPlace.getName());
            textAreasLayout.addView(nameTextView);
        } if (myPlace.getAddress() != null && !myPlace.getAddress().isEmpty()) {
            TextView addressTextView = new TextView(context);
            addressTextView.setTextSize(14);
            addressTextView.setTypeface(null, Typeface.BOLD);
            addressTextView.setElegantTextHeight(true);
            addressTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            addressTextView.setSingleLine(false);
            addressTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            addressTextView.setText(myPlace.getAddress());
            textAreasLayout.addView(addressTextView);
        } if (myPlace.getRating() != null) {
            RatingBar ratingBar = new RatingBar(context, null, android.R.attr.ratingBarStyleSmall);
            ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ratingBar.setIsIndicator(true);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize(0.1f);
            ratingBar.setPadding(5, 0, 5, 0);
            ratingBar.setRating(myPlace.getRating().floatValue());
            linearLayoutHorizontal.addView(ratingBar);
        } if (myPlace.getPhoneNumber() != null && !myPlace.getPhoneNumber().isEmpty()) {
            TextView phoneTextView = new TextView(context);
            phoneTextView.setTextSize(14);
            phoneTextView.setTypeface(null, Typeface.BOLD);
            phoneTextView.setElegantTextHeight(true);
            phoneTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            phoneTextView.setSingleLine(false);
            phoneTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            phoneTextView.setText(myPlace.getPhoneNumber());
            phoneTextView.setPadding(20, 0, 0, 0);
            linearLayoutHorizontal.addView(phoneTextView);
        }
        textAreasLayout.addView(linearLayoutHorizontal);
        return stopCardView;
    }
}
