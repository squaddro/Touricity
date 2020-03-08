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
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.routeList.event.IRouteDraw;
import com.squadro.touricity.view.routeList.event.IRouteSave;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@RequiresApi(api = Build.VERSION_CODES.M)

public class SavedRouteView extends LinearLayout implements ScrollView.OnScrollChangeListener, View.OnLongClickListener {

    @Getter
    private List<Route> routeList;

    @Setter
    private IRouteDraw iRouteDraw;

    @Setter
    @Getter
    private IRouteSave iRouteSave;

    private Route prevHighlighted;
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

        if(routeList == null || routeList.isEmpty()) return;
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
        for(int j=0; j<routes.getChildCount(); j++) {
            RouteCardView routeView = (RouteCardView) routes.getChildAt(j);
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

    @Override
    public boolean onLongClick(View v) {
        Route route = ((RouteCardView) v).getRoute();
        iRouteSave.deleteRoute(route);
        return false;
    }
}
