package com.squadro.touricity.view.map.placesAPI;

import com.google.android.gms.maps.model.Marker;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MarkerInfo {
    private Marker marker;
    private MyPlace myPlace;
    private boolean isNearBy;

    public MarkerInfo(Marker marker,MyPlace myPlace,boolean isNearBy){
        this.marker = marker;
        this.myPlace = myPlace;
        this.isNearBy = isNearBy;
    }
    public boolean getIsNearby(){
        return isNearBy;
    }
}
