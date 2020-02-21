package com.squadro.touricity.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestAPI {

    @POST("route/update")
    Call<JsonObject> updateRoute(@Body JsonObject body);

    @POST("mock/path")
    Call<JsonObject> sendPathRequest(@Body JsonObject body);

    @POST("location/info")
    Call<JsonObject> locationInfo(@Body JsonObject body);

    @POST("create/location")
    Call<JsonObject> createLocation(@Body JsonObject body);

    @POST("filter")
    Call<JsonObject> filter(@Body JsonObject body);
}
