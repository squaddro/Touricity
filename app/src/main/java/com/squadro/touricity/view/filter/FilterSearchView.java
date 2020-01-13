package com.squadro.touricity.view.filter;

import android.content.Context;

import com.squadro.touricity.R;
import com.squadro.touricity.view.PanelLayout;

public class FilterSearchView extends PanelLayout {

    public FilterSearchView(Context context) {
        super(context);
    }

    @Override
    protected int getID() {
        return R.layout.filter_search_view;
    }
}
