package com.squadro.touricity.view.map.placesAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.routeList.entry.StopCardView;
import com.squadro.touricity.view.routeList.event.IEntryButtonEventsListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class StopCardViewHandler {

    private final Context context;
    private StopCardView stopCardView;
    private MyPlace myPlace;
    private String viewId;
    private Stop stop;
    private List<Bitmap> stopImages;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public StopCardViewHandler(StopCardView stopCardView, MyPlace myPlace, Context context,String viewId,Stop stop) {
        this.stopCardView = stopCardView;
        this.myPlace = myPlace;
        this.context = context;
        this.viewId = viewId;
        this.stop = stop;
        stopImages = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public StopCardView putViews() {
        TextView minutesLabel = getLabel(" minutes",16);
        TextView dollarLabel = getLabel(" $",16);
        TextView durationLabel = getLabel("Duration: ",16);
        TextView expenseLabel = getLabel("Expense: ",16);

        LinearLayout linearLayoutVertical = new LinearLayout(context);
        linearLayoutVertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutVertical.setOrientation(VERTICAL);
        linearLayoutVertical.setPadding(10, 5, 10, 5);

        LinearLayout linearLayoutHorizontal = new LinearLayout(context);
        linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutHorizontal.setOrientation(HORIZONTAL);

        LinearLayout durationAndCost = new LinearLayout(context);
        durationAndCost.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        durationAndCost.setOrientation(HORIZONTAL);

        if(viewId.equals("create")){
            RelativeLayout buttons = new RelativeLayout(context);
            buttons.setId(View.generateViewId());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            buttons.setLayoutParams(params);

            Button down = new Button(context);
            down.setClickable(true);
            down.setId(R.id.down_arrow_stop);
            down.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
            down.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_24px);
            down.setOnClickListener(v -> MapFragmentTab2.getRouteCreateView().onMoveEntry(stop, IEntryButtonEventsListener.EDirection.DOWN));

            Button up = new Button(context);
            up.setId(R.id.up_arrow_stop);
            up.setClickable(true);
            RelativeLayout.LayoutParams buttonParam = new RelativeLayout.LayoutParams(150,150);
            buttonParam.addRule(RelativeLayout.RIGHT_OF,R.id.down_arrow_stop);
            up.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_24px);
            up.setLayoutParams(buttonParam);

            up.setOnClickListener(v -> MapFragmentTab2.getRouteCreateView().onMoveEntry(stop, IEntryButtonEventsListener.EDirection.UP));

            Button delete = new Button(context);
            delete.setId(R.id.delete_stop);
            delete.setClickable(true);
            buttonParam = new RelativeLayout.LayoutParams(150,150);
            buttonParam.addRule(RelativeLayout.RIGHT_OF,R.id.up_arrow_stop);
            delete.setBackgroundResource(R.drawable.ic_remove_24px);
            delete.setLayoutParams(buttonParam);
            delete.setOnClickListener(v -> MapFragmentTab2.getRouteCreateView().onRemoveEntry(stop));

            Button streetView = new Button(context);
            streetView.setId(R.id.street_view_button);
            streetView.setClickable(true);
            streetView.setFocusable(true);
            buttonParam = new RelativeLayout.LayoutParams(150,150);
            buttonParam.addRule(RelativeLayout.RIGHT_OF,R.id.delete_stop);
            streetView.setBackgroundResource(R.drawable.ic_streetview_24px);
            streetView.setLayoutParams(buttonParam);
            streetView.setOnClickListener(v -> {
                MapFragmentTab2.streetViewPanorama.setPosition(stop.getLocation().getLatLng());
                MapFragmentTab2.rootView.findViewById(R.id.streetCardViewMap2).setVisibility(View.VISIBLE);

                Button exitStreet = MapFragmentTab2.rootView.findViewById(R.id.streetExitButtonMap2);
                exitStreet.setOnClickListener(v1 -> MapFragmentTab2.rootView.findViewById(R.id.streetCardViewMap2).setVisibility(View.INVISIBLE));
            });

            HorizontalScrollView horizontalScrollView = stopCardView.findViewById(R.id.horizontalScrollView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW,buttons.getId());
            horizontalScrollView.setLayoutParams(layoutParams);

            buttons.addView(down);
            buttons.addView(up);
            buttons.addView(delete);
            buttons.addView(streetView);
            RelativeLayout relativeLayout = stopCardView.findViewById(R.id.stop_card_relative);
            relativeLayout.addView(buttons,0);
        }else if(viewId.equals("explore")||viewId.equals("saved")){
            HorizontalScrollView horizontalScrollView = stopCardView.findViewById(R.id.horizontalScrollView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if(viewId.equals("explore")){
                RelativeLayout buttons = new RelativeLayout(context);
                buttons.setId(View.generateViewId());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                buttons.setLayoutParams(params);

                Button streetView = new Button(context);
                streetView.setId(R.id.street_view_button);
                streetView.setClickable(true);
                streetView.setFocusable(true);
                streetView.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
                streetView.setBackgroundResource(R.drawable.ic_streetview_24px);
                streetView.setOnClickListener(v -> {
                    MapFragmentTab1.streetViewPanorama.setPosition(stop.getLocation().getLatLng());
                    MapFragmentTab1.rootView.findViewById(R.id.streetCardViewMap1).setVisibility(View.VISIBLE);

                    Button exitStreet = MapFragmentTab1.rootView.findViewById(R.id.streetExitButtonMap1);
                    exitStreet.setOnClickListener(v1 -> MapFragmentTab1.rootView.findViewById(R.id.streetCardViewMap1).setVisibility(View.INVISIBLE));
                });
                buttons.addView(streetView);
                RelativeLayout relativeLayout = stopCardView.findViewById(R.id.stop_card_relative);
                relativeLayout.addView(buttons,0);

                layoutParams.addRule(RelativeLayout.BELOW,buttons.getId());
                horizontalScrollView.setLayoutParams(layoutParams);
            }else{
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                horizontalScrollView.setLayoutParams(layoutParams);
            }
        }

        LinearLayout textAreasLayout = stopCardView.findViewById(R.id.textAreas);
        if (myPlace.getPhotos() != null && !myPlace.getPhotos().isEmpty()) {
            fillImageViews();
        } if (myPlace.getName() != null && !myPlace.getName().isEmpty()) {
            TextView nameTextView = getLabel(myPlace.getName(), 20);
            textAreasLayout.addView(nameTextView);
        } if (myPlace.getAddress() != null && !myPlace.getAddress().isEmpty()) {
            TextView addressTextView = getLabel(myPlace.getAddress(),14);
            textAreasLayout.addView(addressTextView);
        } if (myPlace.getRating() != null) {
            RatingBar ratingBar = getRatingBar();
            linearLayoutHorizontal.addView(ratingBar);
        } if (myPlace.getPhoneNumber() != null && !myPlace.getPhoneNumber().isEmpty()) {
            TextView phoneTextView = getLabel(myPlace.getPhoneNumber(),14);
            phoneTextView.setPadding(20, 0, 0, 0);
            linearLayoutHorizontal.addView(phoneTextView);
        }
        textAreasLayout.addView(linearLayoutHorizontal);
        if(viewId.equals("create")){

            EditText durationTextView = getEditText(stop.getDuration(),16);
            durationAndCost.addView(durationLabel);
            durationAndCost.addView(durationTextView);
            durationAndCost.addView(minutesLabel);

            durationTextView.setOnKeyListener((v, keyCode, event) -> {
                try{
                    stop.setDuration(Integer.parseInt(durationTextView.getText().toString()));
                    return false;
                }catch(Exception e){return false;}
            });

            EditText costTextView = getEditText(stop.getExpense(),16);
            expenseLabel.setPadding(10,0,0,0);
            costTextView.setOnKeyListener((v, keyCode, event) -> {
                try{
                    stop.setExpense(Integer.parseInt(costTextView.getText().toString()));
                    return false;
                }catch(Exception e){
                    return false;
                }
            });
            durationAndCost.addView(expenseLabel);
            durationAndCost.addView(costTextView);
            durationAndCost.addView(dollarLabel);

        }else{
            TextView durationTextView = getLabel("Duration: " + stop.getDuration() + " minutes",16);
            TextView expenseTextView = getLabel("Duration: " + stop.getExpense() + " minutes",16);
            durationAndCost.addView(durationTextView);
            durationAndCost.addView(expenseTextView);
        }

        textAreasLayout.addView(durationAndCost);
        return stopCardView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private EditText getEditText(int duration,int textSize) {
        EditText durationTextView = new EditText(context);
        durationTextView.setTextSize(textSize);
        durationTextView.setTypeface(null, Typeface.BOLD);
        durationTextView.setElegantTextHeight(true);
        durationTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        durationTextView.setSingleLine(false);
        durationTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        durationTextView.setText(duration + "");
        durationTextView.setFocusable(true);
        durationTextView.setEnabled(true);
        durationTextView.setClickable(true);
        durationTextView.setFocusableInTouchMode(true);
        return durationTextView;
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

    private void fillImageViews() {
        for (Bitmap bm : myPlace.getPhotos()) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(1024, 720));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bm);
            imageView.setPadding(10, 0, 10, 0);
            LinearLayout viewById = stopCardView.findViewById(R.id.img_layout);
            viewById.addView(imageView);
            Bitmap bitmapImage = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            stopImages.add(bitmapImage);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private TextView getLabel(String text,int textSize) {
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
    public static void addButtonsToView(StopCardView cardView, Context context, Stop stop) {
        RelativeLayout buttons = new RelativeLayout(context);
        buttons.setId(View.generateViewId());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                150);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttons.setLayoutParams(params);

        Button down = new Button(context);
        down.setClickable(true);
        down.setId(R.id.down_arrow_stop);
        down.setLayoutParams(new RelativeLayout.LayoutParams(150,150));
        down.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_24px);
        down.setOnClickListener(v -> MapFragmentTab2.getRouteCreateView().onMoveEntry(stop, IEntryButtonEventsListener.EDirection.DOWN));

        Button up = new Button(context);
        up.setId(R.id.up_arrow_stop);
        up.setClickable(true);
        RelativeLayout.LayoutParams buttonParam = new RelativeLayout.LayoutParams(150,150);
        buttonParam.addRule(RelativeLayout.RIGHT_OF,R.id.down_arrow_stop);
        up.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_24px);
        up.setLayoutParams(buttonParam);

        up.setOnClickListener(v -> MapFragmentTab2.getRouteCreateView().onMoveEntry(stop, IEntryButtonEventsListener.EDirection.UP));

        Button delete = new Button(context);
        delete.setId(R.id.delete_stop);
        delete.setClickable(true);
        buttonParam = new RelativeLayout.LayoutParams(150,150);
        buttonParam.addRule(RelativeLayout.RIGHT_OF,R.id.up_arrow_stop);
        delete.setBackgroundResource(R.drawable.ic_remove_24px);
        delete.setLayoutParams(buttonParam);
        delete.setOnClickListener(v -> MapFragmentTab2.getRouteCreateView().onRemoveEntry(stop));

        TextView textView = cardView.findViewById(R.id.stop_name);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                150);
        layoutParams.addRule(RelativeLayout.BELOW,buttons.getId());
        textView.setLayoutParams(layoutParams);
        RelativeLayout relativeLayout = cardView.findViewById(R.id.arbitrary_stop_input_relative);
        buttons.addView(down);
        buttons.addView(up);
        buttons.addView(delete);
        relativeLayout.addView(buttons,0);
    }

    public List<Bitmap> getStopImages(){
        return stopImages;
    }
}
