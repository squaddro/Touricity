package com.squadro.touricity.requests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.converter.LocationConverter;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocationRequests {

    public LocationRequests() {

    }

    public Location getLocationInfo(String location_id) {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Location> location = new AtomicReference<>(null);
        final LocationConverter locationConverter = new LocationConverter();

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();
        obj.addProperty("location_id", location_id);

        Call<JsonObject> jsonObjectCall = restAPI.locationInfo(obj);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                if (body != null) {
                    location.set((Location) locationConverter.jsonToObject(response.body()));
                }
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return location.get();
    }

    public void createLocation(Location location) {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        Gson gson = new Gson();
        String s = gson.toJson(location);
        JsonParser jsonParser = new JsonParser();
        JsonObject asJsonObject = jsonParser.parse(s).getAsJsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.createLocation(asJsonObject);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String message = response.message();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
            }
        });
    }
}
