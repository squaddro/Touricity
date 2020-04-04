package com.squadro.touricity.view.map.offline;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.MainActivity;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.MapMaths;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WriteOfflineDataAsync extends AsyncTask<Route, Void, SavedRoutesItem> {

    private SavedRouteView savedRouteView;
    private File file;
    private Activity activity;

    public WriteOfflineDataAsync(Activity activity, File file, SavedRouteView savedRouteView) {
        this.activity = activity;
        this.file = file;
        this.savedRouteView = savedRouteView;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected SavedRoutesItem doInBackground(Route... routesArr) {
        if (MainActivity.checkConnection()) {
            DownloadMapTiles downloadMapTiles = new DownloadMapTiles();
            new Thread(() -> {
                downloadMapTiles.downloadTileBounds(MapMaths.getRouteBoundings(routesArr[0]));
            }).start();
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
                    for(MyPlace myPlace : responsePlaces){
                        if(myPlace.getPlace_id().equals(stop.getLocation().getLocation_id())){
                            places.add(myPlace);
                        }
                    }
                }
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<byte[]> bytes = new ArrayList<>();
                if (myPlace.getPhotos() != null) {
                    for (Bitmap bitmap : myPlace.getPhotos()) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        bytes.add(byteArray);
                    }
                }
                savePlaces.add(new MyPlaceSave(myPlace, bytes));
            }
            new XStream().toXML(new SavedRoutesItem(routes, savePlaces), fileWriter);
            return new SavedRoutesItem(routes, savePlaces);
        } else {
            SavedRoutesItem savedRoutes = getSavedRoutes(file);
            List<Route> routes = null;
            List<MyPlaceSave> myPlaces = null;
            if (savedRoutes != null) {
                routes = savedRoutes.getRoutes();
                myPlaces = savedRoutes.getMyPlaces();
            }

            if (routes != null) {
                routes.add(routesArr[0]);
            }

            HashSet<MyPlace> places = new HashSet<>();
            for (IEntry entry : routesArr[0].getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    List<MyPlace> responsePlaces = MapFragmentTab2.responsePlaces;
                    for(MyPlace myPlace : responsePlaces){
                        if(myPlace.getPlace_id().equals(stop.getLocation().getLocation_id())){
                            places.add(myPlace);
                        }
                    }
                }
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<byte[]> bytes = new ArrayList<>();
                if (myPlace.getPhotos() != null) {
                    for (Bitmap bitmap : myPlace.getPhotos()) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        bytes.add(byteArray);
                    }
                }
                savePlaces.add(new MyPlaceSave(myPlace, bytes));
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
            new XStream().toXML(new SavedRoutesItem(routes, myPlaces), fileWriter);
            return new SavedRoutesItem(routes, myPlaces);
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(SavedRoutesItem savedRoutesItem) {
        MapFragmentTab3.savedRoutesItem = savedRoutesItem;
        savedRouteView.setRouteList(savedRoutesItem.getRoutes(), savedRoutesItem.getMyPlaces());
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
