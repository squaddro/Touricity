package com.squadro.touricity.requests;

import com.google.gson.JsonObject;
import com.squadro.touricity.converter.LocationConverter;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocationRequests {

    public LocationRequests() {

        LocationConverter locationConverter = new LocationConverter();

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.sendlocationRequest(obj);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Location location = (Location) locationConverter.jsonToObject(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
            }
        });
    }
}
