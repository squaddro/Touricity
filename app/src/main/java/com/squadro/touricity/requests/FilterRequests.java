package com.squadro.touricity.requests;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.RouteExploreView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
                AtomicInteger count = new AtomicInteger(0);
                AtomicInteger stopCount = new AtomicInteger(0);
                for(Route route : routes){
                    for(IEntry entry : route.getEntries()){
                        if(entry instanceof Stop) stopCount.incrementAndGet();
                    }
                }
                for (Route route : routes) {
                    count.incrementAndGet();
                    for (IEntry entry : route.getEntries()) {
                        if (entry instanceof Stop) {
                            stopCount.decrementAndGet();
                            Stop stop = (Stop) entry;
                            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                                    Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS);
                            FetchPlaceRequest request = FetchPlaceRequest.newInstance(stop.getLocation().getLocation_id(), fields);
                            MapFragmentTab2.placesClient.fetchPlace(request).addOnSuccessListener((placeResponse) -> {
                                Place place = placeResponse.getPlace();
                                List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();
                                List<Bitmap> photos = new ArrayList<>();
                                if (photoMetadatas != null) {
                                    AtomicInteger atomicInteger = new AtomicInteger(0);
                                    for (; atomicInteger.get() < photoMetadatas.size(); atomicInteger.incrementAndGet()) {
                                        PhotoMetadata metadata = photoMetadatas.get(atomicInteger.get());
                                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(metadata)
                                                .setMaxWidth(1024) // Optional.
                                                .setMaxHeight(720) // Optional.
                                                .build();
                                        MapFragmentTab2.placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                            photos.add(bitmap);
                                            if (photos.size() == photoMetadatas.size()) {
                                                MyPlace myPlace = new MyPlace(place, photos);
                                                MapFragmentTab2.responsePlaces.add(myPlace);
                                            }
                                            if(stopCount.get() == 0){
                                                routeExploreView.setRouteList(routes);
                                            }
                                        }).addOnFailureListener(Throwable::printStackTrace);
                                    }
                                } else {
                                    MyPlace myPlace = new MyPlace(place, null);
                                    MapFragmentTab2.responsePlaces.add(myPlace);
                                    if(stopCount.get() == 0){
                                        routeExploreView.setRouteList(routes);
                                    }
                                }
                            }).addOnFailureListener(Throwable::printStackTrace);
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
