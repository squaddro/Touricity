package com.squadro.touricity.view.map.placesAPI;

import com.google.android.gms.maps.model.Marker;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MarkerInfo {
    private Marker marker;
    private MyPlace myPlace;

    public MarkerInfo(Marker marker,MyPlace myPlace){
        this.marker = marker;
        this.myPlace = myPlace;
    }
}
