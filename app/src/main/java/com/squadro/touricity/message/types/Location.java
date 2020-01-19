package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.ILocation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location implements ILocation {

    private String location_id;
    private double latitude;
    private double longitude;

    public Location(String location_id, double latitude, double longitude) {
        this.location_id = location_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
        location_id = null;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
}
