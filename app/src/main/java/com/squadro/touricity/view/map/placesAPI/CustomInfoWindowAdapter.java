package com.squadro.touricity.view.map.placesAPI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squadro.touricity.R;
import com.squadro.touricity.view.map.MapFragmentTab2;

import java.util.List;
import java.util.stream.Collectors;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;
    private CardView cardView;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getInfoContents(Marker marker) {
        List<MarkerInfo> markerInfos = MapFragmentTab2.markerInfoList;
        List<MarkerInfo> collect = markerInfos.stream()
                .filter(markerInfo -> markerInfo.getMarker().getId().equals(marker.getId()))
                .collect(Collectors.toList());

        if (collect.size() > 0) {
            if(collect.get(0).getIsNearby()){
                cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.marker_info_nearby, null);
                MarkerInfoViewHandler markerInfoViewHandler = new MarkerInfoViewHandler(cardView, collect.get(0).getMyPlace(), context);
                markerInfoViewHandler.putViewsForNearby();
            }else{
                cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.marker_info_view, null);
                MarkerInfoViewHandler markerInfoViewHandler = new MarkerInfoViewHandler(cardView, collect.get(0).getMyPlace(), context);
                markerInfoViewHandler.putViews();
            }
        }
        return cardView;
    }
}
