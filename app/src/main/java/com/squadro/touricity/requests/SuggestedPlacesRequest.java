package com.squadro.touricity.requests;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.map.MapFragmentTab1;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.squadro.touricity.view.map.MapFragmentTab1.suggestedMarkerList;

public class SuggestedPlacesRequest {

    private GoogleMap map;
    private LatLng southwest;
    private LatLng northeast;

    private MarkerOptions mo = new MarkerOptions();

    public SuggestedPlacesRequest(GoogleMap map, LatLng southwest, LatLng northeast){
        this.map = map;
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public void getFavPlaces() throws JSONException {
        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();

        obj.addProperty("minLng", southwest.longitude);
        obj.addProperty("maxLng", northeast.longitude);
        obj.addProperty("minLat", southwest.latitude);
        obj.addProperty("maxLat", northeast.latitude);


        Call<JsonObject> jsonObjectCall = restAPI.suggest(obj);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();

                if(body != null){
                    ArrayList<Marker> oldMarkers = MapFragmentTab1.suggestedMarkerList;

                    //make it for all the tabs!

                    for (Marker m:oldMarkers) {
                        if(m != null)
                            m.remove();
                    }

                    JsonArray jPlaces = body.getAsJsonArray("places");
                    ArrayList<LatLng> placeList = new ArrayList<>();

                    for(int i=0; i<jPlaces.size(); i++){
                        JsonObject jLatLon = jPlaces.get(i).getAsJsonObject();
                        placeList.add(new LatLng(jLatLon.get("lat").getAsDouble(), jLatLon.get("lon").getAsDouble()));
                    }

                    MarkerOptions mo;
                    //TODO: set color or icon

                    for(int i=0; i<placeList.size(); i++){
                        if(placeList.get(i) != null){
                            mo = new MarkerOptions();
                            mo.position(new LatLng(placeList.get(i).latitude, placeList.get(i).longitude));
                            suggestedMarkerList.add(map.addMarker(mo));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
                Log.e("ERROR", message);
            }
        });

    }



}
