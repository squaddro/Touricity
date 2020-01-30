package com.squadro.touricity.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestAPI {

    @POST("mock/route")
    Call<JsonObject> sendRouteRequest(@Body JsonObject body);

    @POST("mock/path")
    Call<JsonObject> sendPathRequest(@Body JsonObject body);

    @POST("mock/stop")
    Call<JsonObject> sendStopRequest(@Body JsonObject body);

    @POST("mock/location")
    Call<JsonObject> sendlocationRequest(@Body JsonObject body);
}
