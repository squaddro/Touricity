package com.squadro.touricity.message.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class PathVertex {

    private double lat;
    private double lon;

    public PathVertex(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }
}
