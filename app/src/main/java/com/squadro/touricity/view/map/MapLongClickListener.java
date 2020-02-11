package com.squadro.touricity.view.map;

import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.google.android.gms.maps.GoogleMap;
import com.squadro.touricity.R;
import com.squadro.touricity.view.popupWindowView.PopupWindowView;

import java.util.ArrayList;

public class MapLongClickListener {

    private final GoogleMap googleMap;
    private final FrameLayout frameLayout;

    private double x;
    private double y;
    PopupWindow popupWindow;
    public MapLongClickListener(GoogleMap googleMap, FrameLayout frameLayout) {
        this.googleMap = googleMap;
        this.frameLayout = frameLayout;
        initializeListener();
    }

    private void initializeListener() {
        googleMap.setOnMapLongClickListener(latLng -> {
            Rect rect = new Rect();
            frameLayout.getLocalVisibleRect(rect);

            if(popupWindow != null){
                popupWindow.dismiss();
                popupWindow = null;
            }
            LinearLayout popupLayout = (LinearLayout)frameLayout.inflate(frameLayout.getContext(), R.layout.popup_window, null);

            ArrayList<String> stringArrayList = new ArrayList<String>();
            stringArrayList.add("Add location");
            stringArrayList.add("Add some");
            stringArrayList.add("Add more");

            PopupWindowView popupWindowView = new PopupWindowView(3,stringArrayList,popupLayout);

            popupWindow = new PopupWindow(popupWindowView.getLinearLayout(), LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.showAsDropDown(frameLayout,(int)x,(int)y - (frameLayout.getRootView().getBottom() - frameLayout.getBottom()));

        });
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void dissmissPopUp() {
        if(popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
