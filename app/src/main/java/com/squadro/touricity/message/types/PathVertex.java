package com.squadro.touricity.message.types;

import com.google.android.gms.maps.model.LatLng;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class PathVertex {

    private double latitude;
    private double longitude;

    public PathVertex(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public LatLng toLatLong() {
        return new LatLng(latitude, longitude);
    }
}
