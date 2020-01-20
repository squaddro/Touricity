package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.widget.ExpandableListView;

import com.squadro.touricity.R;
import com.squadro.touricity.view.PanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteListView extends PanelLayout {

    RouteExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    List<String> dataHeaderList;
    HashMap<String, List<RouteListItem>> childDataList;

    public RouteListView(Context context) {
        super(context);
        initializeRouteListView(context);
    }

    private void initializeRouteListView(Context context) {
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            parent.smoothScrollToPosition(groupPosition);
            return false;
        });

        expandableListView.setOnGroupExpandListener(groupPosition -> {

        });

        expandableListView.setOnGroupCollapseListener(groupPosition -> {

        });

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            return false;
        });

        // preparing list data
        prepareListData();

        //our custom adapter for handling data
        listAdapter = new RouteExpandableListAdapter(context, dataHeaderList, childDataList);

        // setting list adapter
        expandableListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        // Some dummy data for testing listview

        dataHeaderList = new ArrayList<String>();
        childDataList = new HashMap<String, List<RouteListItem>>();

        // Adding child data
        dataHeaderList.add("Header 1");
        dataHeaderList.add("Header 2");
        dataHeaderList.add("Header 3");

        // Adding child data

        List<RouteListItem> childList1 = new ArrayList<>();
        List<RouteListItem> childList2 = new ArrayList<>();
        List<RouteListItem> childList3 = new ArrayList<>();

        childDataList.put(dataHeaderList.get(0), childList1);
        childDataList.put(dataHeaderList.get(1), childList2);
        childDataList.put(dataHeaderList.get(2), childList3);
    }
    @Override
    protected int getID() {
        return R.layout.route_list;
    }
}
