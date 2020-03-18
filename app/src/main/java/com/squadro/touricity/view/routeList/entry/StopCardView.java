package com.squadro.touricity.view.routeList.entry;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.requests.ILocationRequest;
import com.squadro.touricity.requests.LocationRequests;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.PolylineDrawer;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.RouteListItem;

import lombok.Setter;

public class StopCardView extends RouteListItem<Stop> implements ILocationRequest {

    private Stop stop;
    private String viewId;

    @Setter
    private Route route;


    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    private TextView textExpense;
    private TextView textComment;
    private TextView textDuration;

    public StopCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() {
        textComment = findViewById(R.id.card_view_comment_content);
        textExpense = findViewById(R.id.card_view_expense_content);
        textDuration = findViewById(R.id.card_view_duration_content);
    }

    @Override
    public void update(Stop stop) {
        this.stop = stop;

        textComment.setText(stop.getComment());
        textDuration.setText(stop.getDuration() + " minutes");
        textExpense.setText(stop.getExpense() + "$");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onLongClick(View view) {
        if (this.getViewId().equals("explore")) {
            String location_id = this.getEntry().getLocation_id();
            RouteCreateView routeCreateView = MapFragmentTab2.getRouteCreateView();

            if (routeCreateView != null) {
                LocationRequests locationRequests = new LocationRequests();
                locationRequests.getLocationInfo(location_id, this);
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(this.getViewId().equals("explore")){

            PolylineDrawer polylineDrawer = new PolylineDrawer(MapFragmentTab1.getMap());
            polylineDrawer.drawRoute(this.route, stop);
        }

        else if(this.getViewId().equals("saved")){

            PolylineDrawer polylineDrawer = new PolylineDrawer(MapFragmentTab3.getMap());
            polylineDrawer.drawRoute(this.route, stop);
        }
    }

    @Override
    protected int getRemoveButtonId() {
        return R.id.stop_view_remove_button;
    }

    @Override
    protected int getMoveUpButtonId() {
        return R.id.stop_view_move_up_button;
    }

    @Override
    protected int getMoveDownButtonId() {
        return R.id.stop_view_move_down_button;
    }

    @Override
    protected int getEditButtonId() {
        return R.id.stop_view_edit_button;
    }

    @Override
    public Stop getEntry() {
        return stop;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResponseLocationInfo(Location location) {
        RouteCreateView routeCreateView = MapFragmentTab2.getRouteCreateView();
        routeCreateView.onInsertLocation(location);
    }
}
