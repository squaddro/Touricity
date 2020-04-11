package com.squadro.touricity.view.map.placesAPI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.routeList.entry.StopCardView;

import java.util.List;
import java.util.stream.Collectors;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static Context context;

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
        List<MarkerInfo> markerInfos;
        if (MainActivity.checkConnection()) {
            markerInfos = MapFragmentTab2.markerInfoList;
        } else {
            markerInfos = MapFragmentTab3.markerInfoList;
        }

        List<MarkerInfo> collect = markerInfos.stream()
                .filter(markerInfo -> markerInfo.getMarker().getPosition().longitude == marker.getPosition().longitude &&
                        markerInfo.getMarker().getPosition().latitude == marker.getPosition().latitude)
                .collect(Collectors.toList());
        CardView cardView = null;
        if (collect.size() > 0) {
            if (collect.get(0).getIsNearby()) {
                cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.marker_info_nearby, null);
                MarkerInfoViewHandler markerInfoViewHandler = new MarkerInfoViewHandler(cardView, collect.get(0).getMyPlace(), context);
                markerInfoViewHandler.putViewsForNearby();
            } else {
                cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.marker_info_view, null);
                MarkerInfoViewHandler markerInfoViewHandler = new MarkerInfoViewHandler(cardView, collect.get(0).getMyPlace(), context);
                markerInfoViewHandler.putViews();
            }
        } else {
            List<Stop> stopCollect = MapFragmentTab2.customStopList.stream()
                    .filter(stop -> stop.getLocation().getLatLng().longitude == marker.getPosition().longitude &&
                            stop.getLocation().getLatLng().latitude == marker.getPosition().latitude)
                    .collect(Collectors.toList());
            if (stopCollect.size() > 0) {
                Stop stop = stopCollect.get(0);
                cardView = getStopCardView(stop);
                cardView.setLayoutParams(new RelativeLayout.LayoutParams(660,660));
            }
        }
        return cardView;
    }

    public static StopCardView getStopCardView(Stop stop) {
        String comment = stop.getComment();
        String title = comment.substring(comment.indexOf("Title:"), comment.indexOf("Desc:"));
        String desc = comment.substring(comment.indexOf("Desc"));

        StopCardView stopCardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.arbitrary_stop_output, null);
        TextView titleTextView = stopCardView.findViewById(R.id.stop_name);
        if (!title.isEmpty())
            titleTextView.setText(title);
        TextView descTextView = stopCardView.findViewById(R.id.stop_desc);
        if(!desc.isEmpty())
            descTextView.setText(desc);
        return stopCardView;
    }
}
