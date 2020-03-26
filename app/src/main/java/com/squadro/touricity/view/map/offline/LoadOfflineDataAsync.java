package com.squadro.touricity.view.map.offline;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadOfflineDataAsync extends AsyncTask<Void, Void, SavedRoutesItem> {

    private final File file;
    private SavedRouteView savedRouteView;
    private XStream xStream = null;

    public LoadOfflineDataAsync(SavedRouteView savedRouteView, File file) {
        this.savedRouteView = savedRouteView;
        this.file = file;
        xStream = new XStream();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected SavedRoutesItem doInBackground(Void ... voids) {
        List<MyPlaceSave> placesFromFile = getPlacesFromFile(file);
        List<Route> routesFromFile = getRoutesFromFile(file);
        return new SavedRoutesItem(routesFromFile,placesFromFile);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(SavedRoutesItem savedRoutesItem) {
        savedRouteView.setRouteList(savedRoutesItem.getRoutes(),savedRoutesItem.getMyPlaces());
    }

    private List<Route> getRoutesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) xStream.fromXML(file)).getRoutes();
            } catch (Exception e) {
                return (ArrayList<Route>) (xStream.fromXML(file));
            }
        }
    }

    private List<MyPlaceSave> getPlacesFromFile(File file) {
        if (file.length() == 0) return null;
        else {
            try {
                return ((SavedRoutesItem) xStream.fromXML(file)).getMyPlaces();
            } catch (Exception e) {
                e.printStackTrace();
                return (ArrayList<MyPlaceSave>) (xStream.fromXML(file));
            }
        }
    }


}
