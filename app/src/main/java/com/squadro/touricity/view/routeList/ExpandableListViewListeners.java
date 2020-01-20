package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class ExpandableListViewListeners {

    private final Context context;
    private ExpandableListView expandableListView;

    public ExpandableListViewListeners(Context context, ExpandableListView expandableListView) {
        this.context = context;
        this.expandableListView = expandableListView;
    }

    public ExpandableListView.OnGroupClickListener getOnGroupClickListener() {
        return (parent, v, groupPosition, id) -> {
            // do some stuff here
            Toast.makeText(context,"Group is clicked",Toast.LENGTH_LONG).show();
            return false;
        };
    }

    public ExpandableListView.OnGroupExpandListener getOnGroupExpandListener() {
        return groupPosition -> {
            // do some stuff here
            Toast.makeText(context,"Group is expanded",Toast.LENGTH_LONG).show();
        };
    }

    public ExpandableListView.OnGroupCollapseListener getOnGroupCollapseListener() {
        return groupPosition -> {
            // do some stuff here
            Toast.makeText(context,"Group is collapsed",Toast.LENGTH_LONG).show();
        };
    }

    public ExpandableListView.OnChildClickListener getOnChildClickListener() {
        return (parent, v, groupPosition, childPosition, id) -> {
            // do some stuff here
            Toast.makeText(context,"Child is clicked",Toast.LENGTH_LONG).show();
            return false;
        };
    }
}
