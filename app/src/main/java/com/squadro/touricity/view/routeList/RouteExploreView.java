package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Route;

import java.util.ArrayList;

import lombok.Getter;
@RequiresApi(api = Build.VERSION_CODES.M)

public class RouteExploreView extends LinearLayout{

    @Getter
    private ArrayList<Route> routeList;

    LinearLayout routes;
    NestedScrollView scrollView;
    private AbstractEntry prevHighlighted;

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
    }
}
