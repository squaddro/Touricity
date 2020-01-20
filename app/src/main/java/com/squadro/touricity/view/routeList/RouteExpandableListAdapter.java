package com.squadro.touricity.view.routeList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.squadro.touricity.R;

import java.util.HashMap;
import java.util.List;

public class RouteExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> dataHeaderList;
    private HashMap<String, List<RouteListItem>> childDataList;

    public RouteExpandableListAdapter(Context context, List<String> dataHeaderList, HashMap<String, List<RouteListItem>> childDataList) {
        this.context = context;
        this.dataHeaderList = dataHeaderList;
        this.childDataList = childDataList;
    }

    @Override
    public int getGroupCount() {
        return this.dataHeaderList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childDataList.get(this.dataHeaderList.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.dataHeaderList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childDataList.get(this.dataHeaderList.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView headerLabel = (TextView) convertView
                .findViewById(R.id.list_header);
        headerLabel.setTypeface(null, Typeface.BOLD);
        headerLabel.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.list_item_label);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
