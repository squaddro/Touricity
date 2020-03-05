package com.squadro.touricity.requests;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squadro.touricity.message.types.FilterResult;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.routeList.RouteExploreView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.squadro.touricity.view.filter.Filter;

public class FilterRequests {

    private final RouteExploreView routeExploreView;

    public FilterRequests(RouteExploreView routeExploreView){
        this.routeExploreView = routeExploreView;
    }

    public void filter(Filter filter){

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
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                routeExploreView.setRouteList( (gson.fromJson(response.body().getAsString(), FilterResult.class).getRouteList()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String message = t.getMessage();
                Log.e("ERROR", message);
            }
        });
    }
}
