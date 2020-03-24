package com.squadro.touricity.view.map.placesAPI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squadro.touricity.R;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.routeList.entry.StopCardView;

import java.util.List;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;
    private StopCardView stopCardView;

    public CustomInfoWindowAdapter(Context context) {
        stopCardView = (StopCardView) LayoutInflater.from(context).inflate(R.layout.stopcardview, null);
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
        List<MyPlace> responsePlaces = MapFragmentTab2.responsePlaces;

        if (responsePlaces != null && responsePlaces.size() != 0) {
            StopCardViewHandler stopCardViewHandler = new StopCardViewHandler(stopCardView
                    , MapFragmentTab2.responsePlaces.get(0), context, "create", null);
            stopCardViewHandler.putViews();
        }
    //    stopCardView.setLayoutParams(new LinearLayout.LayoutParams(600,600));
        return stopCardView;
    }
}
