package com.squadro.touricity.view.map;

import android.content.Context;

import com.squadro.touricity.R;
import com.squadro.touricity.view.PanelLayout;

public class MapView extends PanelLayout {

    private static int tabIndex;

    public MapView(Context context) {
        super(context);
    }

    @Override
    protected int getID() {
        int layoutId = 0;
        switch (tabIndex) {
            case 0:
                layoutId = R.layout.tab1_map_view;
                break;
            case 1:
                layoutId = R.layout.tab2_map_view;
                break;
            case 2:
                layoutId = R.layout.tab3_map_view;
                break;
        }
        return layoutId;
    }

    public static void setTabIndex(int tabIndex) {
        MapView.tabIndex = tabIndex;
    }
}
