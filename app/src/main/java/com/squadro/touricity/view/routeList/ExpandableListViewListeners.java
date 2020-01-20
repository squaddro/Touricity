package com.squadro.touricity.view.routeList;

import android.widget.ExpandableListView;

public class ExpandableListViewListeners {

    private ExpandableListView expandableListView;

    public ExpandableListViewListeners(ExpandableListView expandableListView) {
        this.expandableListView = expandableListView;
    }

    public ExpandableListView.OnGroupClickListener getOnGroupClickListener() {
        return (parent, v, groupPosition, id) -> {
            // do some stuff here
            return false;
        };
    }

    public ExpandableListView.OnGroupExpandListener getOnGroupExpandListener() {
        return groupPosition -> {
            // do some stuff here
        };
    }

    public ExpandableListView.OnGroupCollapseListener getOnGroupCollapseListener() {
        return groupPosition -> {
            // do some stuff here
        };
    }

    public ExpandableListView.OnChildClickListener getOnChildClickListener() {
        return (parent, v, groupPosition, childPosition, id) -> {
            // do some stuff here
            return false;
        };
    }
}
