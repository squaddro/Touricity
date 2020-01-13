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

    public Location(double latitude, double longitude) {
        //TODO: uuid will be obtained.
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
}
