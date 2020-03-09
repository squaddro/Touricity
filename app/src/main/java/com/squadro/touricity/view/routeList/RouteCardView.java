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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.routeList.entry.PathCardView;
import com.squadro.touricity.view.routeList.entry.StopCardView;

public class RouteCardView extends CardView implements View.OnClickListener, View.OnLongClickListener {

    private Route route;
    private TextView textRouteId;
    private TextView textCreator;
    private TextView textEntries;
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

    public void loadRoute(Route route) {

        Context context = getContext();
        this.route = route;

        textRouteId.setText(route.getRoute_id());
        textCreator.setText(route.getCreator());
        textEntries.setText("");
        for (IEntry entry : route.getAbstractEntryList()) {
            if (entry instanceof Stop) {
                Stop stop = (Stop) entry;
                textEntries.append(stop.getStop_id() + " ");
                StopCardView cardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
                cardView.setViewId(this.viewId);
                cardView.update(stop);
                RelativeLayout bLayer = cardView.findViewById(R.id.stop_view_button_layer);
                bLayer.setVisibility(INVISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
                bLayer.setLayoutParams(params);
                entryList.addView(cardView);
            } else if (entry instanceof Path) {
                Path path = (Path) entry;
                textEntries.append(path.getPath_id() + " ");
                PathCardView cardView = (PathCardView) LayoutInflater.from(context).inflate(R.layout.path_card_view, null);
                cardView.update(path);
                RelativeLayout bLayer = cardView.findViewById(R.id.path_view_button_layer);
                bLayer.setVisibility(INVISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
                bLayer.setLayoutParams(params);
                entryList.addView(cardView);
            }
        }
    }

    protected void initialize() {
        textRouteId = findViewById(R.id.card_view_route_id_content);
        textCreator = findViewById(R.id.card_view_creator_content);
        textEntries = findViewById(R.id.card_view_route_entries_content);
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
