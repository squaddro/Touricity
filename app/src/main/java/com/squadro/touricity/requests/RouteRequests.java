package com.squadro.touricity.requests;

import com.google.gson.JsonObject;
import com.squadro.touricity.converter.RouteConverter;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RouteRequests {

    public RouteRequests() {

        RouteConverter routeConverter = new RouteConverter();

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.sendRouteRequest(obj);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Route route = (Route) routeConverter.jsonToObject(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
            }
        });
    }
}
