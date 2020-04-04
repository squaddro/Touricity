package com.squadro.touricity.view.routeList;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squadro.touricity.HomeActivity;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Comment;
import com.squadro.touricity.message.types.CommentRegister;
import com.squadro.touricity.message.types.Like;
import com.squadro.touricity.message.types.LikeRegister;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.requests.CommentRequest;
import com.squadro.touricity.requests.LikeRequest;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.map.placesAPI.StopCardViewHandler;
import com.squadro.touricity.view.routeList.entry.StopCardView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

public class RouteCardView extends CardView implements View.OnClickListener, View.OnLongClickListener {

    @Getter
    private Route route;
    private LinearLayout entryList;
    private ViewFlipper viewFlipper;
    LinearLayout likeCommentView;
    private EditText commentText;
    private String viewId;
    private List<Bitmap> stopImages;
    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public RouteCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        stopImages = new ArrayList<>();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadRoute(Route route) {

        Context context = getContext();
        this.route = route;
        List<MyPlace> placesList = MapFragmentTab2.responsePlaces;
        for (IEntry entry : route.getAbstractEntryList()) {
            if (entry instanceof Stop) {
                Stop stop = (Stop) entry;
                List<MyPlace> collect = placesList.stream().filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                        .collect(Collectors.toList());
                StopCardView cardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
                cardView.setRoute(route);
                if(collect.size() > 0){
                    StopCardViewHandler stopCardViewHandler = new StopCardViewHandler(cardView,collect.get(0),context,"explore",stop);
                    cardView = stopCardViewHandler.putViews();
                    stopImages.addAll(stopCardViewHandler.getStopImages());
                }
                cardView.setViewId(this.viewId);
                cardView.update(stop);
                entryList.addView(cardView);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadRoute(Route route,List<MyPlaceSave> myPlaces) {

        Context context = getContext();
        this.route = route;
        for (IEntry entry : route.getAbstractEntryList()) {
            if (entry instanceof Stop) {
                Stop stop = (Stop) entry;

                List<MyPlace> places = new ArrayList<>();

                for(MyPlaceSave myPlaceSave : myPlaces){
                    List<Bitmap> bitmapList = new ArrayList<>();
                    for(byte [] bytes : myPlaceSave.getPhotos()){
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmapList.add(decodedByte);
                    }
                    places.add(new MyPlace(myPlaceSave,bitmapList));
                }

                List<MyPlace> collect = places.stream().filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                        .collect(Collectors.toList());
                StopCardView cardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
                cardView.setRoute(route);
                if(collect.size() > 0){
                    StopCardViewHandler stopCardViewHandler = new StopCardViewHandler(cardView,collect.get(0),context,"saved",stop);
                    cardView = stopCardViewHandler.putViews();
                    stopImages.addAll(stopCardViewHandler.getStopImages());
                }
                cardView.setViewId(this.viewId);
                cardView.update(stop);
                entryList.addView(cardView);
            }
        }
    }

    protected void initialize() {
        entryList = findViewById(R.id.route_entries_list);
        likeCommentView = (LinearLayout)findViewById(R.id.like_comment_view);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        ImageButton micButton = likeCommentView.findViewById(R.id.mic_comment);
        commentText = likeCommentView.findViewById(R.id.PostCommentDesc);
        micButton.setOnClickListener(v -> {
            HomeActivity.fragment1.initializeSpeechToText(commentText);
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initialize();
        setOnClickListener(this);
        setOnLongClickListener(this);
        setLongClickable(true);
        getLikeComment();
    }

    public void setViewFlipper(ViewFlipper viewFlipper){
        for(Bitmap imageShown : stopImages){
            ImageView imageViewShown = new ImageView(this.getContext());
            imageViewShown.setImageBitmap(imageShown);

            viewFlipper.addView(imageViewShown);
            viewFlipper.setFlipInterval(4000);
            viewFlipper.setAutoStart(true);
        }
    }

    private void getLikeComment() {
        Context context = getContext();
        Button pushCommentButton = findViewById(R.id.button_send_comment);
        pushCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Comment comment = new Comment();
                EditText commentDesc = (EditText)findViewById(R.id.PostCommentDesc);
                comment.setCommentDesc(commentDesc.getText().toString());
                CommentRegister commentRegister = new CommentRegister(MainActivity.credential.getUser_name(), comment, route.getRoute_id());
                CommentRequest commentRequest = new CommentRequest(context,likeCommentView);
                try {
                    commentRequest.postComment(commentRegister);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton pushLikeButton = findViewById(R.id.button_send_like);
        pushLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Like like = new Like();
                RatingBar ratingBar = (RatingBar) findViewById(R.id.routeLikeBar);
                like.setScore((int)ratingBar.getRating());
                LikeRegister likeRegister = new LikeRegister(MainActivity.credential.getUser_name(), like, route.getRoute_id());
                LikeRequest likeRequest = new LikeRequest(getContext());
                try {
                    likeRequest.postLike(likeRegister);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView showComments = findViewById(R.id.link_comments);
        showComments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentRequest commentRequest = new CommentRequest(context, likeCommentView);
                LinearLayout layout = (LinearLayout) likeCommentView.findViewById(R.id.comment_list);
                layout.removeAllViews();
                ImageButton buttonUp = (ImageButton) likeCommentView.findViewById(R.id.imageButtonUp);
                buttonUp.setVisibility(VISIBLE);
                try {
                    commentRequest.getComment(route.getRoute_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton buttonUp = findViewById(R.id.imageButtonUp);
        buttonUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) likeCommentView.findViewById(R.id.comment_list);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                lp.height = 0;
                layout.setLayoutParams(lp);
                ImageButton buttonDown = findViewById(R.id.imageButtonDown);
                buttonDown.setVisibility(VISIBLE);
                buttonUp.setVisibility(INVISIBLE);
            }
        });

        ImageButton buttonDown = findViewById(R.id.imageButtonDown);
        buttonDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) likeCommentView.findViewById(R.id.comment_list);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;;
                layout.setLayoutParams(lp);
                ImageButton buttonUp = findViewById(R.id.imageButtonUp);
                buttonUp.setVisibility(VISIBLE);
                buttonDown.setVisibility(INVISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.route_entries_list);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layout.getLayoutParams();

        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(INVISIBLE);
            lp.height = 0;
            layout.setLayoutParams(lp);
        } else {
            layout.setVisibility(VISIBLE);
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layout.setLayoutParams(lp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onLongClick(View v) {
        if(getViewId().equals("saved")){
            MapFragmentTab3.getSavedRouteView().onLongClick(v);
        }
        else if(getViewId().equals("explore")){
            MapFragmentTab1.getRouteExploreView().onLongClick(v);
        }

        return true;
    }

    public Route getRoute() {
        return route;
    }
}
