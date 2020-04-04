package com.squadro.touricity.view.map.offline;

import android.app.Activity;
import android.graphics.Bitmap;
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

public class WriteOfflineDataAsync extends AsyncTask<Route, Integer, RoutePlace> {

    private RouteCardView routeCardView;
    private File file;
    private Activity activity;
    private ProgressBar progressBar;

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
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected RoutePlace doInBackground(Route... routesArr) {

        if (MainActivity.checkConnection()) {
            DownloadMapTiles downloadMapTiles = new DownloadMapTiles();
            //      new Thread(() -> {
            //           downloadMapTiles.downloadTileBounds(MapMaths.getRouteBoundings(routesArr[0]));
            //s     }).start();
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
            int count = 0;
            for(MyPlace myPlace : places){
                count += myPlace.getPhotos().size();
            }
            int gap = 100 / (count + 4);
            int progressCount = 0;
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
                        progressCount += gap;
                        publishProgress(progressCount);
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
            int count = 0;
            for(MyPlace myPlace : places){
                if(myPlace.getPhotos() != null){
                    count += myPlace.getPhotos().size();
                }
            }
            int gap = 100 / (count + 4);
            int progressCount = 0;

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
                        progressCount += gap;
                        publishProgress(progressCount);
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
        progressBar.setProgress(100);
        progressBar.setVisibility(View.INVISIBLE);
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
