package com.squadro.touricity.requests;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.view.map.MapFragmentTab1;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab4;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.RouteCreateView;
import com.squadro.touricity.view.routeList.RouteExploreView;
import com.squadro.touricity.view.routeList.RouteSuggestionView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class GetPlacesInfoAsync extends AsyncTask<Stop, Void, Void> {

    private ProgressBar progressBar;
    private CountDownLatch countDownLatch;
    private Route route;
    private RouteExploreView routeExploreView;
    private RouteSuggestionView routeSuggestionView;
    private RouteCreateView routeCreateView;
    private double score;

    public GetPlacesInfoAsync(Route route, RouteExploreView routeExploreView, double score, CountDownLatch countDownLatch,
                              ProgressBar progressBar) {
        this.route = route;
        this.routeExploreView = routeExploreView;
        this.score = score;
        this.countDownLatch = countDownLatch;
        this.progressBar = progressBar;
        this.routeSuggestionView = null;
    }

    public GetPlacesInfoAsync(Route route, RouteSuggestionView routeSuggestionView, double score, CountDownLatch countDownLatch,
                              ProgressBar progressBar) {
        this.route = route;
        this.routeSuggestionView = routeSuggestionView;
        this.score = score;
        this.countDownLatch = countDownLatch;
        this.progressBar = progressBar;
        this.routeExploreView = null;
    }

    public GetPlacesInfoAsync(Route route, RouteCreateView routeCreateView, double score, CountDownLatch countDownLatch,
                              ProgressBar progressBar) {
        this.route = route;
        this.routeCreateView = routeCreateView;
        this.score = score;
        this.countDownLatch = countDownLatch;
        this.progressBar = progressBar;
        this.routeExploreView = null;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected Void doInBackground(Stop... stops) {

        if(routeExploreView != null && routeSuggestionView == null){
            Stop stop = stops[0];
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL);
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
                                if(!MapFragmentTab2.isPlaceExist(myPlace)) MapFragmentTab2.responsePlaces.add(myPlace);
                                countDownLatch.countDown();
                                if(countDownLatch.getCount() == 0){
                                    routeExploreView.addRoute(route,score);
                                    progressBar.setProgress(progressBar.getProgress()+2);
                                }
                                if(progressBar.getProgress() == progressBar.getMax()){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    MapFragmentTab1.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }
                            }

                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                    progressBar.setProgress(progressBar.getProgress()+1);
                } else {
                    MyPlace myPlace = new MyPlace(place, null);
                    if(!MapFragmentTab2.isPlaceExist(myPlace)) MapFragmentTab2.responsePlaces.add(myPlace);
                    countDownLatch.countDown();
                    if(countDownLatch.getCount() == 0){
                        routeExploreView.addRoute(route,score);
                        progressBar.setProgress(progressBar.getProgress()+2);
                    }
                    if(progressBar.getProgress() == progressBar.getMax()){
                        progressBar.setVisibility(View.INVISIBLE);
                        MapFragmentTab1.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    progressBar.setProgress(progressBar.getProgress()+1);
                }
            }).addOnFailureListener(Throwable::printStackTrace);
            return null;
        }

        else if (routeExploreView == null && routeSuggestionView != null){
            Stop stop = stops[0];
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL);
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
                                if(!MapFragmentTab2.isPlaceExist(myPlace)) MapFragmentTab2.responsePlaces.add(myPlace);
                                countDownLatch.countDown();
                                if(countDownLatch.getCount() == 0){
                                    routeSuggestionView.addRoute(route,score);
                                    progressBar.setProgress(progressBar.getProgress()+2);
                                }
                                if(progressBar.getProgress() == progressBar.getMax()){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    MapFragmentTab4.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }
                            }

                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                    progressBar.setProgress(progressBar.getProgress()+1);
                } else {
                    MyPlace myPlace = new MyPlace(place, null);
                    if(!MapFragmentTab2.isPlaceExist(myPlace)) MapFragmentTab2.responsePlaces.add(myPlace);
                    countDownLatch.countDown();
                    if(countDownLatch.getCount() == 0){
                        routeSuggestionView.addRoute(route,score);
                        progressBar.setProgress(progressBar.getProgress()+2);
                    }
                    if(progressBar.getProgress() == progressBar.getMax()){
                        progressBar.setVisibility(View.INVISIBLE);
                        MapFragmentTab4.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    progressBar.setProgress(progressBar.getProgress()+1);
                }
            }).addOnFailureListener(Throwable::printStackTrace);
            return null;
        }

        else if (routeExploreView == null && routeSuggestionView == null && routeCreateView != null){
            Stop stop = stops[0];
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL);
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
                                if(!MapFragmentTab2.isPlaceExist(myPlace)) MapFragmentTab2.responsePlaces.add(myPlace);
                                countDownLatch.countDown();
                                if(countDownLatch.getCount() == 0){
                                    //routeSuggestionView.addRoute(route,score);
                                    //progressBar.setProgress(progressBar.getProgress()+2);
                                }
                            }

                        }).addOnFailureListener(Throwable::printStackTrace);
                    }
                    //progressBar.setProgress(progressBar.getProgress()+1);
                } else {
                    MyPlace myPlace = new MyPlace(place, null);
                    if(!MapFragmentTab2.isPlaceExist(myPlace)) MapFragmentTab2.responsePlaces.add(myPlace);
                    countDownLatch.countDown();
                    if(countDownLatch.getCount() == 0){
                        //routeSuggestionView.addRoute(route,score);
                        //progressBar.setProgress(progressBar.getProgress()+2);
                    }
//                    if(progressBar.getProgress() == progressBar.getMax()){
                        //progressBar.setVisibility(View.INVISIBLE);
    //                    MapFragmentTab4.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  //                  }
                    //progressBar.setProgress(progressBar.getProgress()+1);
                }
            }).addOnFailureListener(Throwable::printStackTrace);
            return null;
        }

        return null;
    }
}

