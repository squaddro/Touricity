package com.squadro.touricity.view.routeList;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.map.placesAPI.StopCardViewHandler;
import com.squadro.touricity.view.routeList.entry.StopCardView;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

public class RouteCardView extends CardView implements View.OnClickListener, View.OnLongClickListener {

    @Getter
    private Route route;
    private LinearLayout entryList;
    private String viewId;

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public RouteCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
                }
                cardView.setViewId(this.viewId);
                cardView.update(stop);
                entryList.addView(cardView);
            }
        }
    }

    protected void initialize() {
        entryList = findViewById(R.id.route_entries_list);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initialize();
        setOnClickListener(this);
        setOnLongClickListener(this);
        setLongClickable(true);
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
        return true;
    }

    public Route getRoute() {
        return route;
    }
}
