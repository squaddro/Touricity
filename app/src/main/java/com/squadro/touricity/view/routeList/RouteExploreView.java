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
import android.widget.ScrollView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.PolylineDrawer;
import com.squadro.touricity.view.routeList.event.IRouteDraw;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@RequiresApi(api = Build.VERSION_CODES.M)

public class RouteExploreView extends LinearLayout implements ScrollView.OnScrollChangeListener{

    @Getter
    private ArrayList<Route> routeList;

    @Setter
    private IRouteDraw iRouteDraw;

    LinearLayout routes;
    NestedScrollView scrollView;
    private Route prevHighlighted;

    public RouteExploreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRouteList(ArrayList<Route> routeList) {
        this.routeList = routeList;
        UpdateView();
    }

    private void UpdateView() {
        CleanView();

        Context context = getContext();

        for(int i = 0; i<routeList.size(); i++){
            Route route = routeList.get(i);
            RouteCardView cardView = (RouteCardView) LayoutInflater.from(context).inflate(R.layout.route_card_view, null);
            cardView.setViewId("explore");
            cardView.loadRoute(route);
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
            RouteCardView routeView = (RouteCardView) routes.getChildAt(i);
            Route route = routeView.getRoute();
            if(routeView.getLocalVisibleRect(rect)){
                if(route != null && (prevHighlighted != route)){
                    iRouteDraw.drawHighlighted(route);
                }
                prevHighlighted = route;

                break;
            }
        }
    }
}
