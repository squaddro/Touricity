package com.squadro.touricity.view.map.placesAPI;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface INearByResponse {
    void onPlacesResponse(List<String> placesIds, LatLng latLng);
}
