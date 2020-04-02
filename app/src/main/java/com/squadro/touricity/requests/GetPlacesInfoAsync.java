package com.squadro.touricity.requests;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.RouteExploreView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class GetPlacesInfoAsync extends AsyncTask<Stop, Void, Void> {

    private CountDownLatch countDownLatch;
    private Route route;
    private RouteExploreView routeExploreView;
    private double score;

    public GetPlacesInfoAsync(Route route, RouteExploreView routeExploreView, double score, CountDownLatch countDownLatch) {
        this.route = route;
        this.routeExploreView = routeExploreView;
        this.score = score;
        this.countDownLatch = countDownLatch;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected Void doInBackground(Stop... stops) {

        Stop stop = stops[0];
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
                            countDownLatch.countDown();
                            if(countDownLatch.getCount() == 0){
                                routeExploreView.addRoute(route,score);
                            }
                        }

                    }).addOnFailureListener(Throwable::printStackTrace);
                }
            } else {
                MyPlace myPlace = new MyPlace(place, null);
                MapFragmentTab2.responsePlaces.add(myPlace);
                countDownLatch.countDown();
                if(countDownLatch.getCount() == 0){
                    routeExploreView.addRoute(route,score);
                }
            }
        }).addOnFailureListener(Throwable::printStackTrace);
        return null;
    }
}

