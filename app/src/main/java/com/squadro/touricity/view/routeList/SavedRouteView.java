package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;

import java.util.List;

import lombok.Getter;
@RequiresApi(api = Build.VERSION_CODES.M)

public class SavedRouteView extends LinearLayout{

    @Getter
    private List<Route> routeList;

    LinearLayout routes;
    NestedScrollView scrollView;

    public SavedRouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
        UpdateView();
    }

    private void UpdateView() {
        CleanView();

        Context context = getContext();

        for(int i = 0; i<routeList.size(); i++){
            Route route = routeList.get(i);
            RouteCardView cardView = (RouteCardView) LayoutInflater.from(context).inflate(R.layout.route_card_view, null);
            cardView.setViewId("saved");
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

        routes = findViewById(R.id.route_save_list);
        scrollView = findViewById(R.id.route_save_scroll);
    }
}
