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
    private String city_id;

    public Location(String location_id, String city_id, double latitude, double longitude) {
        this.location_id = location_id;
        this.city_id = city_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
        location_id = null;
        city_id = null;
        latitude = 0.0;
        longitude = 0.0;
    }
}
