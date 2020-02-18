package com.squadro.touricity.view.map;

import android.graphics.Rect;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.gms.maps.GoogleMap;
import com.squadro.touricity.R;
import com.squadro.touricity.view.popupWindowView.PopupWindowParameters;
import com.squadro.touricity.view.popupWindowView.PopupWindowView;

import java.util.List;

public class MapLongClickListener {

    private final GoogleMap googleMap;
    private final FrameLayout frameLayout;
    private int topPeekHeight;
    private int bottomPeekHeight;
    private double x;
    private double y;
    private PopupWindowParameters popupWindowParameters;
    private PopupWindowView popupWindowView;
    private PopupWindow popupWindow;

    public MapLongClickListener(GoogleMap googleMap, FrameLayout frameLayout, int topPeekHeight, int bottomPeekHeight, PopupWindowParameters popupWindowParameters) {
        this.googleMap = googleMap;
        this.frameLayout = frameLayout;
        this.topPeekHeight = topPeekHeight;
        this.bottomPeekHeight = bottomPeekHeight;
        this.popupWindowParameters = popupWindowParameters;
        initializeListener();
    }

    private void initializeListener() {
        googleMap.setOnMapLongClickListener(latLng -> {

            if (popupWindow != null) {
                popupWindow.dismiss();
                popupWindow = null;
            }

            Rect rect = new Rect();

            frameLayout.getGlobalVisibleRect(rect);
            if (frameLayout.getRootView().getBottom() - ((int) y) <= frameLayout.getRootView().getBottom() - frameLayout.getBottom()) {
                return;
            }

            if ((int) y <= rect.top + topPeekHeight || frameLayout.getRootView().getBottom() - ((int) y) <= bottomPeekHeight) {
                return;
            }
            LinearLayout popupLayout = (LinearLayout) frameLayout.inflate(frameLayout.getContext(), R.layout.popup_window, null);

            popupWindowView = new PopupWindowView(popupWindowParameters, popupLayout);

            popupWindow = new PopupWindow(popupWindowView.getLinearLayout(), LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            int xPosition = (int) x - popupWindowView.getWidth();
            int yPosition = frameLayout.getRootView().getBottom() - ((int) y) - (int) (((double) popupWindowParameters.getNumberOfButtons() / 2.0) * (popupWindowView.getHeight()));

            if (yPosition < frameLayout.getRootView().getBottom() - frameLayout.getBottom()) {
                yPosition += frameLayout.getRootView().getBottom() - frameLayout.getBottom() - yPosition;
            }

            popupWindow.showAtLocation(frameLayout, Gravity.BOTTOM | Gravity.LEFT, xPosition, yPosition);
        });
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setTopPeekHeight(int topPeekHeight) {
        this.topPeekHeight = topPeekHeight;
    }

    public void setBottomPeekHeight(int bottomPeekHeight) {
        this.bottomPeekHeight = bottomPeekHeight;
    }

    public void dissmissPopUp() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public List<Button> getButtons(){
        return popupWindowView.getButtons();
    }
}
