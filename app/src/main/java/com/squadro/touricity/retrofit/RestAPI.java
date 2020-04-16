package com.squadro.touricity.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @POST("signin")
    Call<JsonObject> signin(@Body JsonObject body);

    @POST("signup")
    Call<JsonObject> signup(@Body JsonObject body);

    @GET("api/place/nearbysearch/json?&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE")
    Call<JsonObject> getNearbyPlaces(@Query("location") String location, @Query("radius") int radius);

    @POST("create/comment")
    Call<JsonObject> postComment(@Body JsonObject body);

    @POST("create/like")
    Call<JsonObject> postLike(@Body JsonObject body);

    @POST("get/comment")
    Call<JsonObject> getComment(@Body JsonObject body);

    @POST("suggest")
    Call<JsonObject> suggest(@Body JsonObject body);

    @POST("suggestRoutes")
    Call<JsonObject> suggestRoutes(@Body JsonObject body);
}
