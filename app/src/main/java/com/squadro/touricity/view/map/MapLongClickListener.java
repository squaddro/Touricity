package com.squadro.touricity.view.map;

import android.graphics.Rect;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.gms.maps.GoogleMap;
import com.squadro.touricity.R;
import com.squadro.touricity.view.popupWindowView.PopupWindowView;

import java.util.ArrayList;
import java.util.List;

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

            if (popupWindow != null) {
                popupWindow.dismiss();
                popupWindow = null;
            }

            LinearLayout popupLayout = (LinearLayout) frameLayout.inflate(frameLayout.getContext(), R.layout.popup_window, null);
            
            /*This constructor simply adds buttons to popup window. Takes, number of buttons, names of buttons as list and
            * popup window layout as a parameter.
            *  */
            List<String> names = new ArrayList<String>();
            names.add("Button 1");
            names.add("Button 2");
            names.add("Button 3");

            PopupWindowView popupWindowView = new PopupWindowView(3, names, popupLayout);

            popupWindow = new PopupWindow(popupWindowView.getLinearLayout(), LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            int yPosition = (int) y - (frameLayout.getRootView().getBottom() - frameLayout.getBottom()) + popupWindowView.getHeight();

            popupWindow.showAsDropDown(frameLayout, (int) x, yPosition);

        });
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void dissmissPopUp() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
