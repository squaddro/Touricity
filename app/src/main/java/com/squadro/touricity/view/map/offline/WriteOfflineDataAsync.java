package com.squadro.touricity.view.map.offline;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ProgressBar;

import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.RouteCardView;
import com.squadro.touricity.view.routeList.RoutePlace;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WriteOfflineDataAsync extends AsyncTask<Route, Integer, RoutePlace> {

    private RouteCardView routeCardView;
    private File file;
    private Activity activity;
    private ProgressBar progressBar;
    private AtomicInteger count = new AtomicInteger(0);

    public WriteOfflineDataAsync(Activity activity, File file, RouteCardView routeCardView) {
        this.activity = activity;
        this.file = file;
        this.routeCardView = routeCardView;
        progressBar = routeCardView.findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected RoutePlace doInBackground(Route... routesArr) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        DownloadMapTiles downloadMapTiles = new DownloadMapTiles();
        AtomicReference<Map<String, String>> urlsAndFileNames = new AtomicReference<>();
        if (MainActivity.checkConnection()) {
            urlsAndFileNames.set(downloadMapTiles.downloadTileBounds(MapMaths.getRouteBoundings(routesArr[0])));
            atomicInteger.set(urlsAndFileNames.get().size());
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.length() == 0) {
            List<Route> routes = new ArrayList<>();
            routes.add(routesArr[0]);
            HashSet<MyPlace> places = new HashSet<>();
            for (IEntry entry : routesArr[0].getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    List<MyPlace> responsePlaces = MapFragmentTab2.responsePlaces;
                    for (MyPlace myPlace : responsePlaces) {
                        if (myPlace.getPlace_id().equals(stop.getLocation().getLocation_id())) {
                            places.add(myPlace);
                        }
                    }
                }
            }

            for (MyPlace myPlace : places) {
                if(myPlace.getPhotos() != null)
                    atomicInteger.set(atomicInteger.get() + myPlace.getPhotos().size());
            }
            progressBar.setMax(atomicInteger.get() + 4);
            if(urlsAndFileNames.get() != null){
                new Thread(() -> downloadMapTiles.download(urlsAndFileNames.get(),progressBar,count)).start();
            }
            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<String> photoIds = new ArrayList<>();
                if (myPlace.getPhotos() != null) {
                    for (Bitmap bitmap : myPlace.getPhotos()) {
                        String generationId = bitmap.getGenerationId() + "";
                        File root = new File(activity.getApplicationContext().getFilesDir(), "PlacePhotos");
                        if (!root.exists()) {
                            root.mkdir();
                        }
                        File file = new File(root, generationId + ".png");
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        publishProgress(progressBar.getProgress() + 1);
                        photoIds.add(generationId);
                    }
                }
                savePlaces.add(new MyPlaceSave(myPlace, photoIds));
            }
            SavedRoutesItem savedRoutesItem = new SavedRoutesItem(routes, savePlaces);
            MapFragmentTab3.savedRoutesItem = savedRoutesItem;
            new XStream().toXML(savedRoutesItem, fileWriter);
            return new RoutePlace(routes, new ArrayList<>(places));
        } else {
            SavedRoutesItem savedRoutes = getSavedRoutes(file);
            List<Route> routes = null;
            List<MyPlaceSave> myPlaces = null;
            if (savedRoutes != null) {
                routes = savedRoutes.getRoutes();
                myPlaces = savedRoutes.getMyPlaces();
            }
            if(routes != null){
                routes.add(routesArr[0]);
            }
            HashSet<MyPlace> places = new HashSet<>();
            for (IEntry entry : routesArr[0].getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    List<MyPlace> responsePlaces = MapFragmentTab2.responsePlaces;
                    for (MyPlace myPlace : responsePlaces) {
                        if (myPlace.getPlace_id().equals(stop.getLocation().getLocation_id())) {
                            places.add(myPlace);
                        }
                    }
                }
            }
            for (MyPlace myPlace : places) {
                if(myPlace.getPhotos() != null){
                    atomicInteger.set(atomicInteger.get() + myPlace.getPhotos().size());
                }
            }
            progressBar.setMax(atomicInteger.get() + 4);

            if(urlsAndFileNames.get() != null){
                new Thread(() ->downloadMapTiles.download(urlsAndFileNames.get(),progressBar,count)).start();
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<String> photoIds = new ArrayList<>();
                if (myPlace.getPhotos() != null) {
                    for (Bitmap bitmap : myPlace.getPhotos()) {
                        String generationId = bitmap.getGenerationId() + "";
                        File root = new File(activity.getApplicationContext().getFilesDir(), "PlacePhotos");
                        if (!root.exists()) {
                            root.mkdir();
                        }
                        File file = new File(root, generationId + ".png");
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        publishProgress(progressBar.getProgress() + 1);
                        photoIds.add(generationId);
                    }
                }
                savePlaces.add(new MyPlaceSave(myPlace, photoIds));
            }
            if (myPlaces != null) {
                myPlaces.addAll(savePlaces);
            }

            file.delete();
            try {
                file.createNewFile();
                fileWriter = new FileWriter(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SavedRoutesItem savedRoutesItem = new SavedRoutesItem(routes, myPlaces);
            MapFragmentTab3.savedRoutesItem = savedRoutesItem;
            new XStream().toXML(savedRoutesItem, fileWriter);
            return new RoutePlace(routes, new ArrayList<>(places));
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(RoutePlace routePlace) {
        count.incrementAndGet();
        MapFragmentTab3.progressDone(progressBar,count);
    }

    private SavedRoutesItem getSavedRoutes(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) new XStream().fromXML(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
