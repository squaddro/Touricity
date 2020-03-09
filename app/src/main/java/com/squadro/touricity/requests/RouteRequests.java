package com.squadro.touricity.requests;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.converter.RouteConverter;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.routeList.IRouteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RouteRequests {

    public RouteRequests() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateRoute(Route route, IRouteResponse iRouteResponse) {
        RouteConverter routeConverter = new RouteConverter();

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        Gson gson = new Gson();
        String jsonString = gson.toJson(route);
        jsonString = jsonString.replace("abstractEntryList", "entries");
        JsonParser jsonParser = new JsonParser();
        JsonObject asJsonObject = jsonParser.parse(jsonString).getAsJsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.updateRoute(asJsonObject);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                iRouteResponse.onRouteResponse((Route) routeConverter.jsonToObject(response.body()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
            }
        });
    }
}
