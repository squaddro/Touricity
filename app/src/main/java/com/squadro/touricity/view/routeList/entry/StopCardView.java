package com.squadro.touricity.view.routeList.entry;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.requests.ILocationRequest;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.PolylineDrawer;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.RouteListItem;

import lombok.Getter;
import lombok.Setter;

public class StopCardView extends RouteListItem<Stop> implements ILocationRequest {

    public enum ProgressStatus {
        NOT_TRACKED,
        ON_POINT,
        TRACKED
    }

    @Getter
    private Stop stop;
    private String viewId;
    private boolean collapsed = false;

    @Setter
    private Route route;

    private int expandedHeight=0;

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public StopCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        if(viewId != null && viewId.equals("progress")) {
            collapse();
        }
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void update(Stop stop) {
        this.stop = stop;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onLongClick(View view) {
        if (this.getViewId().equals("explore")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("WARNING")
                    .setMessage("This action will add this stop to the route in the create tab. Do you want to continue?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        RouteCreateView routeCreateView = MapFragmentTab2.getRouteCreateView();
                        if (routeCreateView != null) {
                            routeCreateView.onInsertStop(stop);
                        }
                    }).setNegativeButton("No",(dialog, which) -> {})
                    .show();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if (this.getViewId().equals("explore")) {

            PolylineDrawer polylineDrawer = new PolylineDrawer(MapFragmentTab1.getMap(), viewId,getContext());
            polylineDrawer.drawRoute(this.route, stop);
        } else if (this.getViewId().equals("saved")) {

            PolylineDrawer polylineDrawer = new PolylineDrawer(MapFragmentTab3.getMap(), viewId,getContext());
            polylineDrawer.drawRoute(this.route, stop);
        }
        else if(getViewId().equals("progress")) {
            if(collapsed)
                expanse();
            else
                collapse();
        }
    }


    @Override
    public Stop getEntry() {
        return stop;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResponseLocationInfo(Location location) {
        RouteCreateView routeCreateView = MapFragmentTab2.getRouteCreateView();
        routeCreateView.onInsertLocation(location);
    }

    public void collapse() {
        findViewById(R.id.stop_card_relative).setVisibility(INVISIBLE);
        findViewById(R.id.stop_card_collapsed).setVisibility(VISIBLE);
        collapsed = true;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        expandedHeight = params.height;
        params.height = 450;
        setLayoutParams(params);
        invalidate();
    }

    private void expanse() {
        findViewById(R.id.stop_card_relative).setVisibility(VISIBLE);
        findViewById(R.id.stop_card_collapsed).setVisibility(INVISIBLE);
        collapsed = false;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.height = expandedHeight;
        setLayoutParams(params);
        invalidate();
    }

    public void changeProgressStatus(ProgressStatus status) {
        switch (status) {
            case TRACKED:
                setCardBackgroundColor(Color.DKGRAY);
                break;
            case ON_POINT:
                setCardBackgroundColor(Color.LTGRAY);
                break;
            case NOT_TRACKED:
                setCardBackgroundColor(Color.WHITE);
                break;
        }
    }
}
