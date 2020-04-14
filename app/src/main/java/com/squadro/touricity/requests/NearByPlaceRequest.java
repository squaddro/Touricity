package com.squadro.touricity.requests;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.map.placesAPI.INearByResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NearByPlaceRequest {
    public NearByPlaceRequest(){}

    public void findNearbyPlaces(LatLng latLng,int radius,INearByResponse iNearByResponse){
        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofitForGoogle();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<JsonObject> jsonObjectCall = restAPI.getNearbyPlaces(latLng.latitude+","+latLng.longitude,radius);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray results =  response.body().get("results").getAsJsonArray();
                List<String> placeIds = new ArrayList<>();
                for(JsonElement element : results){
                    JsonElement id = element.getAsJsonObject().get("place_id");
                    placeIds.add(id.getAsString());
                }
                iNearByResponse.onPlacesResponse(placeIds,latLng);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.fillInStackTrace();
            }
        });
    }
}
