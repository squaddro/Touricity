package com.squadro.touricity.requests;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.converter.RouteConverter;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.filter.Filter;
import com.squadro.touricity.view.routeList.RouteExploreView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FilterRequests {

    private final RouteExploreView routeExploreView;

    public FilterRequests(RouteExploreView routeExploreView) {
        this.routeExploreView = routeExploreView;
    }

    public void filter(Filter filter) {

        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        Gson gson = new Gson();
        String jsonString = gson.toJson(filter);
        JsonParser jsonParser = new JsonParser();
        JsonObject asJsonObject = jsonParser.parse(jsonString).getAsJsonObject();

        Call<JsonObject> jsonObjectCall = restAPI.filter(asJsonObject);

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray routeList = response.body().getAsJsonArray("routeList");
                ArrayList<Route> routes = new ArrayList<>();
                RouteConverter routeConverter = new RouteConverter();
                for (JsonElement element : routeList) {
                    routes.add((Route) routeConverter.jsonToObject(element.getAsJsonObject()));
                }
                for (Route route : routes) {
                    for (IEntry entry : route.getEntries()) {
                        if (entry instanceof Stop) {
                            Stop stop = (Stop) entry;
                            GetPlacesInfoAsync getPlacesInfoAsync = new GetPlacesInfoAsync(route,routeExploreView);
                            getPlacesInfoAsync.execute(stop);
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
