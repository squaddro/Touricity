package com.squadro.touricity.requests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.converter.RouteConverter;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;

import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RouteRequests {

    public RouteRequests() {

    }

    public Route updateRoute(Route route){
        AtomicReference<Route> atomicRoute = new AtomicReference<>();
        RouteConverter routeConverter = new RouteConverter();

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        Gson gson = new Gson();
        String jsonString = gson.toJson(route);

        JsonParser jsonParser = new JsonParser();
        JsonObject asJsonObject = jsonParser.parse(jsonString).getAsJsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.updateRoute(asJsonObject);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                atomicRoute.set((Route) routeConverter.jsonToObject(response.body()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
            }
        });
        return atomicRoute.get();
    }
}
