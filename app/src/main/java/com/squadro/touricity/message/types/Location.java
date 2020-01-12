package com.squadro.touricity.message.types;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder(toBuilder = true)
public class Location {

    private String location_id;
    private double latitude;
    private double longitude;

}
