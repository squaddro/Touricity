package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.ILocation;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder(toBuilder = true)
public class Location implements ILocation {

    private String location_id;
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        location_id = null;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
}
