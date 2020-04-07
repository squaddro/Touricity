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
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.DirectionsAPI.RouteMerger;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.routeList.event.IRouteDraw;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@RequiresApi(api = Build.VERSION_CODES.N)

public class RouteExploreView extends LinearLayout implements ScrollView.OnScrollChangeListener{

    @Getter
    private List<Route> routeList;

    @Setter
    private IRouteDraw iRouteDraw;

    LinearLayout routes;
    NestedScrollView scrollView;
    private Route prevHighlighted;

    public RouteExploreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
        UpdateView();
    }

    public void addRoute(Route route, double score){
        if(routeList == null){
            routeList = new ArrayList<>();
        }
        routeList.add(0,route);
        RouteCardView cardView = (RouteCardView) LayoutInflater.from(getContext()).inflate(R.layout.route_card_view, null);
        cardView.setViewId("explore");
        cardView.loadRoute(route);
        ViewFlipper stopImages = cardView.findViewById(R.id.view_flipper);
        cardView.setViewFlipper(stopImages);
        routes.addView(cardView,0);
        routes.invalidate();
        RatingBar ratingBar = cardView.findViewById(R.id.routeRatingBar);
        ratingBar.setRating((float) score);
    }
    private void UpdateView() {
        CleanView();

        Context context = getContext();
        if(routeList.isEmpty()) return;
        for(int i = 0; i<routeList.size(); i++){
            Route route = routeList.get(i);
            RouteCardView cardView = (RouteCardView) LayoutInflater.from(context).inflate(R.layout.route_card_view, null);
            cardView.setViewId("explore");
            cardView.loadRoute(route);
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

        routes = findViewById(R.id.route_explore_list);
        scrollView = findViewById(R.id.route_explore_scroll);
        scrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        Rect rect = new Rect();
        scrollView.getHitRect(rect);
        for(int j=0; j<routes.getChildCount(); j++) {
            RouteCardView routeView = (RouteCardView) routes.getChildAt(j);
            Route route = routeView.getRoute();
            if(routeView.getLocalVisibleRect(rect)){
                if((iRouteDraw != null) && (route != null) && (prevHighlighted != route)){
                    iRouteDraw.drawHighlighted(route);
                }
                prevHighlighted = route;

                break;
            }
        }
    }

    public void onLongClick(View v) {
        Route route = ((RouteCardView) v).getRoute();
        RouteMerger rm = new RouteMerger(MapFragmentTab2.getRouteCreateView());
        rm.mergeRoute(route);
        Toast.makeText(getContext(), "Route is merged!",
                Toast.LENGTH_LONG).show();
    }
}
