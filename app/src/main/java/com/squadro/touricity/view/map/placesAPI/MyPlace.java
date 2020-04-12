package com.squadro.touricity.view.map.placesAPI;

import android.graphics.Bitmap;
import android.icu.text.SymbolTable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.squadro.touricity.view.routeList.MyPlaceSave;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MyPlace {

    private String address;
    @NonNull
    private String place_id;
    @NonNull
    private LatLng latLng;
    private String name;
    private List<Bitmap> photos;
    private String phoneNumber;
    private Double rating;
    private Integer priceLevel;

    public MyPlace(String address, String place_id, LatLng latLng, String name,
                   List<Bitmap> photos, String phoneNumber, Double rating, Integer priceLevel) {
        this.address = address;
        this.place_id = place_id;
        this.latLng = latLng;
        this.name = name;
        this.photos = photos;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.priceLevel = priceLevel;
    }

    public MyPlace(Place place,List<Bitmap> photos) {
        this.address = place.getAddress();
        this.place_id = place.getId();
        this.latLng = place.getLatLng();
        this.name = place.getName();
        this.photos = photos;
        this.phoneNumber = place.getPhoneNumber();
        this.rating = place.getRating();
        this.priceLevel = place.getPriceLevel();
    }

    public MyPlace(MyPlace place, List<Bitmap> photos) {
        this.address = place.getAddress();
        this.place_id = place.getPlace_id();
        this.latLng = place.getLatLng();
        this.name = place.getName();
        this.photos = photos;
        this.phoneNumber = place.getPhoneNumber();
        this.rating = place.getRating();
        this.priceLevel = place.getPriceLevel();
    }

    public MyPlace(MyPlaceSave place, List<Bitmap> photos) {
        this.address = place.getAddress();
        this.place_id = place.getPlace_id();
        this.latLng = place.getLatLng();
        this.name = place.getName();
        this.photos = photos;
        this.phoneNumber = place.getPhoneNumber();
        this.rating = place.getRating();
        this.priceLevel = place.getPriceLevel();
    }
}
