package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.routeList.event.IRouteSave;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@RequiresApi(api = Build.VERSION_CODES.N)

public class SavedRouteView extends LinearLayout implements ScrollView.OnScrollChangeListener, View.OnLongClickListener {

    @Getter
    private List<Route> routeList = new ArrayList<>();

    @Setter
    private IRouteDraw iRouteDraw;

    @Setter
    @Getter
    private IRouteSave iRouteSave;

    private Route prevHighlighted;
    private LinearLayout routes;
    private NestedScrollView scrollView;
    private TextView routeTitleView;

    private List<MyPlace> places;

    public SavedRouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRouteList(List<Route> routeList, List<MyPlace> places) {
        this.routeList = routeList;
        this.places = places;
        if(routeList != null && routeList.size() > 0){
            iRouteDraw.drawHighlighted(routeList.get(0));
        }
        UpdateView();
    }

    public RouteCardView addRoute(Route route){
        if(routeList == null){
            routeList = new ArrayList<>();
        }
        routeList.add(0,route);
        RouteCardView cardView = (RouteCardView) LayoutInflater.from(getContext()).inflate(R.layout.route_card_view_save, null);
        cardView.setViewId("saved");
        cardView.loadRoute(route);
        ViewFlipper stopImages = cardView.findViewById(R.id.view_flipper);
        cardView.setViewFlipper(stopImages);
        String routeTitle = route.getTitle();
        RelativeLayout routeTitleLayout = cardView.findViewById(R.id.routeTitleTextLayout);
        routeTitleView = cardView.findViewById(R.id.routeTitleTextViewSave);
        if(routeTitle.equals("null")){
            routeTitleLayout.setVisibility(View.INVISIBLE);
        }
        else
            routeTitleView.setText(routeTitle);
        routes.addView(cardView,0);
        routes.invalidate();
        return cardView;
    }

    private void UpdateView() {
        CleanView();

        Context context = getContext();

        if (routeList == null || routeList.isEmpty() || places == null || places.isEmpty()) return;
        for (int i = 0; i < routeList.size(); i++) {
            Route route = routeList.get(i);
            RouteCardView cardView = (RouteCardView) LayoutInflater.from(context).inflate(R.layout.route_card_view_save, null);
            cardView.setViewId("saved");
            cardView.loadRoute(route,places);
            String routeTitle = route.getTitle();
            RelativeLayout routeTitleLayout = cardView.findViewById(R.id.routeTitleTextLayout);
            routeTitleView = cardView.findViewById(R.id.routeTitleTextViewSave);
            if(routeTitle.equals("null")){
                routeTitleLayout.setVisibility(View.INVISIBLE);
            }
            else
                routeTitleView.setText(routeTitle);
            ViewFlipper stopImages = cardView.findViewById(R.id.view_flipper);
            cardView.setViewFlipper(stopImages);
            routes.addView(cardView);
        }
    }

    private void CleanView() {
        routes.removeAllViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setOnLongClickListener(this);
        setLongClickable(true);
        routes = findViewById(R.id.route_save_list);
        scrollView = findViewById(R.id.route_save_scroll);
        scrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        Rect rect = new Rect();
        scrollView.getHitRect(rect);
        for (int j = 0; j < routes.getChildCount(); j++) {
            RouteCardView routeView = (RouteCardView) routes.getChildAt(j);
            Route route = routeView.getRoute();
            if (routeView.getLocalVisibleRect(rect)) {
                if (route != null && (prevHighlighted != route)) {
                    iRouteDraw.drawHighlighted(route);
                }
                prevHighlighted = route;

                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Route route = ((RouteCardView) v).getRoute();
        /*routes.removeView(v);
        iRouteSave.deleteRoute(route);*/
        iRouteSave.startProgress(route);

        return false;
    }
}
