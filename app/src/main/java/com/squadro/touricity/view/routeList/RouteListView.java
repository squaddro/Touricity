package com.squadro.touricity.view.routeList;

import android.content.Context;

import com.squadro.touricity.R;
import com.squadro.touricity.view.PanelLayout;

public class RouteListView extends PanelLayout {

    public RouteListView(Context context) {
        super(context);
    }

    @Override
    protected int getID() {
        return R.layout.route_list;
    }
}
