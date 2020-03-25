package com.squadro.touricity.view.map.placesAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squadro.touricity.R;

import static android.widget.LinearLayout.HORIZONTAL;

public class MarkerInfoViewHandler {

    private final Context context;
    private CardView markerInfoCardView;
    private MyPlace myPlace;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MarkerInfoViewHandler(CardView markerInfoCardView, MyPlace myPlace, Context context) {
        this.markerInfoCardView = markerInfoCardView;
        this.myPlace = myPlace;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CardView putViews() {

        LinearLayout linearLayoutHorizontal = new LinearLayout(context);
        linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutHorizontal.setOrientation(HORIZONTAL);

        LinearLayout textAreasLayout = markerInfoCardView.findViewById(R.id.textAreasMarker);
        if (myPlace.getPhotos() != null && !myPlace.getPhotos().isEmpty()) {
            Bitmap bm = myPlace.getPhotos().get(0);
            ImageView imageView = markerInfoCardView.findViewById(R.id.info_img);
            imageView.setImageBitmap(bm);
        } else {
            ImageView imageView = markerInfoCardView.findViewById(R.id.info_img);
            imageView.setBackgroundResource(R.drawable.img);
        }
        if (myPlace.getName() != null && !myPlace.getName().isEmpty()) {
            TextView nameTextView = getLabel(myPlace.getName(), 16);
            textAreasLayout.addView(nameTextView);
        }
        if (myPlace.getAddress() != null && !myPlace.getAddress().isEmpty()) {
            TextView addressTextView = getLabel(myPlace.getAddress(), 14);
            textAreasLayout.addView(addressTextView);
        }
        if (myPlace.getRating() != null) {
            RatingBar ratingBar = getRatingBar();
            linearLayoutHorizontal.addView(ratingBar);
        }
        if (myPlace.getPhoneNumber() != null && !myPlace.getPhoneNumber().isEmpty()) {
            TextView phoneTextView = getLabel(myPlace.getPhoneNumber(), 14);
            phoneTextView.setPadding(20, 0, 0, 0);
            linearLayoutHorizontal.addView(phoneTextView);
        }
        textAreasLayout.addView(linearLayoutHorizontal);
        return markerInfoCardView;
    }

    private RatingBar getRatingBar() {
        RatingBar ratingBar = new RatingBar(context, null, android.R.attr.ratingBarStyleSmall);
        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ratingBar.setIsIndicator(true);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.1f);
        ratingBar.setPadding(5, 0, 5, 0);
        ratingBar.setRating(myPlace.getRating().floatValue());
        return ratingBar;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private TextView getLabel(String text, int textSize) {
        TextView label = new TextView(context);
        label.setTextSize(textSize);
        label.setTypeface(null, Typeface.BOLD);
        label.setElegantTextHeight(true);
        label.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        label.setSingleLine(false);
        label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        label.setText(text);
        return label;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CardView putViewsForNearby(){
        LinearLayout linearLayoutHorizontal = new LinearLayout(context);
        linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutHorizontal.setOrientation(HORIZONTAL);

        LinearLayout textAreasLayout = markerInfoCardView.findViewById(R.id.textAreasNearby);
        if (myPlace.getPhotos() != null && !myPlace.getPhotos().isEmpty()) {
            Bitmap bm = myPlace.getPhotos().get(0);
            ImageView imageView = markerInfoCardView.findViewById(R.id.info_img_nearby);
            imageView.setImageBitmap(bm);
        } else {
            ImageView imageView = markerInfoCardView.findViewById(R.id.info_img_nearby);
            imageView.setBackgroundResource(R.drawable.img);
        }
        if (myPlace.getName() != null && !myPlace.getName().isEmpty()) {
            TextView nameTextView = getLabel(myPlace.getName(), 16);
            textAreasLayout.addView(nameTextView);
        }
        if (myPlace.getAddress() != null && !myPlace.getAddress().isEmpty()) {
            TextView addressTextView = getLabel(myPlace.getAddress(), 14);
            textAreasLayout.addView(addressTextView);
        }
        if (myPlace.getRating() != null) {
            RatingBar ratingBar = getRatingBar();
            linearLayoutHorizontal.addView(ratingBar);
        }
        if (myPlace.getPhoneNumber() != null && !myPlace.getPhoneNumber().isEmpty()) {
            TextView phoneTextView = getLabel(myPlace.getPhoneNumber(), 14);
            phoneTextView.setPadding(20, 0, 0, 0);
            linearLayoutHorizontal.addView(phoneTextView);
        }
        textAreasLayout.addView(linearLayoutHorizontal);
        return markerInfoCardView;
    }
}
