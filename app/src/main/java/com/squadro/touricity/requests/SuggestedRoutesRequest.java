package com.squadro.touricity.requests;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squadro.touricity.R;
import com.squadro.touricity.converter.RouteConverter;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.RouteLike;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab4;
import com.squadro.touricity.view.routeList.RouteSuggestionView;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SuggestedRoutesRequest {

    private final RouteSuggestionView routeSuggestionView;
    private final Context context;
    private ProgressBar progressBar;


    public SuggestedRoutesRequest(Context context, RouteSuggestionView rsw){
        this.routeSuggestionView = rsw;
        this.context = context;
        this.progressBar = MapFragmentTab4.rootView.findViewById(R.id.progressBarSuggestion);
    }

    public void getFavRoutes(){
        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();

        obj.addProperty("dummy", "dummy");
        Call<JsonObject> jsonObjectCall = restAPI.suggestRoutes(obj);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                routeSuggestionView.setRouteList(new ArrayList<>());
                JsonArray routeList = response.body().getAsJsonArray("routeList");

                ArrayList<RouteLike> routes = new ArrayList<>();
                RouteConverter routeConverter = new RouteConverter();
                int count = 0;
                for (JsonElement element : routeList) {
                    RouteLike routeLike = new RouteLike();
                    routeLike.setScore(element.getAsJsonObject().get("likeScore").getAsDouble());
                    JsonObject routeObject = (JsonObject) element.getAsJsonObject().get("route");
                    routeLike.setRoute((Route) routeConverter.jsonToObject(routeObject));
                    routes.add(routeLike);
                    count += getStopCount(routeLike.getRoute());
                }
                count += routes.size()*2;
                progressBar.setVisibility(View.VISIBLE);

                for (RouteLike routeLike : routes) {
                    int stopCount = getStopCount(routeLike.getRoute());
                    progressBar.setMax(count);
                    CountDownLatch countDownLatch = new CountDownLatch(stopCount);
                    for (IEntry entry : routeLike.getRoute().getEntries()) {
                        if (entry instanceof Stop) {
                            Stop stop = (Stop) entry;
                            if(stop.getComment().isEmpty()){
                                GetPlacesInfoAsync getPlacesInfoAsync = new GetPlacesInfoAsync(routeLike.getRoute(),routeSuggestionView,routeLike.getScore(),
                                        countDownLatch,progressBar);
                                getPlacesInfoAsync.execute(stop);
                            }else{
                                if(!MapFragmentTab2.isStopExist(stop)){
                                    MapFragmentTab2.customStopList.add(stop);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getStopCount(Route route) {
        return route.getAbstractEntryList().stream().filter(iEntry -> iEntry instanceof Stop && !iEntry.getComment().isEmpty())
                .collect(Collectors.toList()).size();
    }
}
