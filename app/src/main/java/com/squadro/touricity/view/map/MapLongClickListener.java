package com.squadro.touricity.view.map;

import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;

public class MapLongClickListener {

    private final GoogleMap googleMap;
    private final FrameLayout frameLayout;

    private double x;
    private double y;

    public MapLongClickListener(GoogleMap googleMap, FrameLayout frameLayout) {
        this.googleMap = googleMap;
        this.frameLayout = frameLayout;
        initializeListener();
    }

    private void initializeListener() {
        googleMap.setOnMapLongClickListener(latLng -> {
            System.out.println(frameLayout.getId());
            System.out.println("xxxxxxxxxxxxx " + x);
            System.out.println("xxxxxxxxxxxxx " + y);
        });
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
