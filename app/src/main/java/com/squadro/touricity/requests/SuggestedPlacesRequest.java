package com.squadro.touricity.requests;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.retrofit.RestAPI;
import com.squadro.touricity.retrofit.RetrofitCreate;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.squadro.touricity.view.map.MapFragmentTab2.suggestedMarkerList;

public class SuggestedPlacesRequest {

    private GoogleMap map;
    private LatLng southwest;
    private LatLng northeast;

    private MarkerOptions mo = new MarkerOptions();

    public SuggestedPlacesRequest(GoogleMap map, LatLng southwest, LatLng northeast){
        this.map = map;
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public void getFavPlaces() throws JSONException {
        RetrofitCreate retrofitCreate = new RetrofitCreate();
        Retrofit retrofit = retrofitCreate.createRetrofit();
        RestAPI restAPI = retrofit.create(RestAPI.class);

        JsonObject obj = new JsonObject();

        obj.addProperty("minLng", southwest.longitude);
        obj.addProperty("maxLng", northeast.longitude);
        obj.addProperty("minLat", southwest.latitude);
        obj.addProperty("maxLat", northeast.latitude);


        Call<JsonObject> jsonObjectCall = restAPI.suggest(obj);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();

                if(body != null){
                    ArrayList<Marker> oldMarkers = MapFragmentTab2.suggestedMarkerList;

                    for (Marker m:oldMarkers) {
                        if(m != null)
                            m.remove();
                    }

                    JsonArray jPlaces = body.getAsJsonArray("locationList");

                    if(jPlaces != null && jPlaces.size() > 0){
                        ArrayList<Location> placeList = new ArrayList<>();

                        for(int i=0; i<jPlaces.size(); i++){
                            JsonObject jLatLon = jPlaces.get(i).getAsJsonObject();
                            placeList.add(new Location(jLatLon.get("location_id").getAsString(),jLatLon.get("latitude").getAsDouble(), jLatLon.get("longitude").getAsDouble()));
                        }

                        MarkerOptions mo;
                        for(int i=0; i<placeList.size(); i++){
                            if(placeList.get(i) != null){
                                mo = new MarkerOptions();
                                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
                                mo.position(new LatLng(placeList.get(i).getLatitude(), placeList.get(i).getLongitude()));
                                suggestedMarkerList.add(map.addMarker(mo));
                                //Stop dummyStop = new Stop(null, 0,0,"",placeList.get(i),null);
                                //GetPlacesInfoAsync getPlacesInfoAsync = new GetPlacesInfoAsync(MapFragmentTab2.getRouteCreateView().getRoute(),MapFragmentTab2.getRouteCreateView(),0.0,new CountDownLatch(5),null);
                                //getPlacesInfoAsync.execute(dummyStop);
                            }
                        }
                    }
                    else{
                        //TODO: ask places api!
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
