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
import com.squadro.touricity.maths.MapMaths;
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
import java.util.List;
import java.util.stream.Collectors;

public class WriteOfflineDataAsync extends AsyncTask<Route,Void,SavedRoutesItem> {

    private SavedRouteView savedRouteView;
    private File file;
    private Activity activity;
    public WriteOfflineDataAsync(Activity activity, File file, SavedRouteView savedRouteView){
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
            List<MyPlace> places = new ArrayList<>();
            for (IEntry entry : routesArr[0].getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    places.addAll(MapFragmentTab2.responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                            .collect(Collectors.toList()));
                }
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<byte[]> bytes = new ArrayList<>();
                for (Bitmap bitmap : myPlace.getPhotos()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bytes.add(byteArray);
                }
                savePlaces.add(new MyPlaceSave(myPlace, bytes));
            }
            new XStream().toXML(new SavedRoutesItem(routes, savePlaces), fileWriter);
            return new SavedRoutesItem(routes,savePlaces);
        } else {
            List<Route> routes = getRoutesFromFile(file);
            List<MyPlaceSave> myPlaces = getPlacesFromFile(file);

            if (routes != null) {
                routes.add(routesArr[0]);
            }

            List<MyPlace> places = new ArrayList<>();
            for (IEntry entry : routesArr[0].getAbstractEntryList()) {
                if (entry instanceof Stop) {
                    Stop stop = (Stop) entry;
                    places.addAll(MapFragmentTab2.responsePlaces.stream()
                            .filter(myPlace -> myPlace.getPlace_id().equals(stop.getLocation().getLocation_id()))
                            .collect(Collectors.toList()));
                }
            }

            List<MyPlaceSave> savePlaces = new ArrayList<>();
            for (MyPlace myPlace : places) {
                List<byte[]> bytes = new ArrayList<>();
                for (Bitmap bitmap : myPlace.getPhotos()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bytes.add(byteArray);
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
            return new SavedRoutesItem(routes,myPlaces);
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(SavedRoutesItem savedRoutesItem) {
        savedRouteView.setRouteList(savedRoutesItem.getRoutes(), savedRoutesItem.getMyPlaces());
    }

    private List<Route> getRoutesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) new XStream().fromXML(file)).getRoutes();
            } catch (Exception e) {
                return (ArrayList<Route>) (new XStream().fromXML(file));
            }
        }
    }

    private List<MyPlaceSave> getPlacesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) new XStream().fromXML(file)).getMyPlaces();
            } catch (Exception e) {
                e.printStackTrace();
                return (ArrayList<MyPlaceSave>) (new XStream().fromXML(file));
            }
        }
    }
}
