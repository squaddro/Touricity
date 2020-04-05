package com.squadro.touricity.view.routeList;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.view.map.placesAPI.MyPlace;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MyPlaceSave {
    private String address;
    @NonNull
    private String place_id;
    @NonNull
    private LatLng latLng;
    private String name;
    private List<String> photosIds;
    private String phoneNumber;
    private Double rating;


    public MyPlaceSave(MyPlace place, List<String> photosIds) {
        this.address = place.getAddress();
        this.place_id = place.getPlace_id();
        this.latLng = place.getLatLng();
        this.name = place.getName();
        this.photosIds = photosIds;
        this.phoneNumber = place.getPhoneNumber();
        this.rating = place.getRating();
    }
}
