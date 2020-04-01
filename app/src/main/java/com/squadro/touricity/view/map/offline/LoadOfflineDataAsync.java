package com.squadro.touricity.view.map.offline;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoadOfflineDataAsync extends AsyncTask<Void, Void, SavedRoutesItem> {

    private final File file;
    private Context context;
    private SavedRouteView savedRouteView;
    private XStream xStream = null;
    private boolean isDelete;
    private Route routeToBeDeleted;
    private ProgressDialog progressDialog;

    public LoadOfflineDataAsync(SavedRouteView savedRouteView, File file, boolean isDelete, Route routeToBeDeleted, Context context) {
        this.savedRouteView = savedRouteView;
        this.file = file;
        this.isDelete = isDelete;
        this.routeToBeDeleted = routeToBeDeleted;
        this.context = context;
        xStream = new XStream();
    }

    @Override
    protected void onPreExecute() {
        if(!isDelete){
            progressDialog = ProgressDialog.show(context,"INFO","Please wait while the offline data loading...");
        }
        super.onPreExecute();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected SavedRoutesItem doInBackground(Void ... voids) {
        if(file.length() == 0) return null;
        List<MyPlaceSave> placesFromFile = getPlacesFromFile(file);
        for(MyPlaceSave myPlaceSave : placesFromFile){
            List<Bitmap> bitmapList = new ArrayList<>();
            for(byte [] bytes : myPlaceSave.getPhotos()){
                Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmapList.add(decodedByte);
            }
            MapFragmentTab3.responsePlaces.add(new MyPlace(myPlaceSave,bitmapList));
        }
        List<Route> routesFromFile = getRoutesFromFile(file);
        return new SavedRoutesItem(routesFromFile,placesFromFile);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(SavedRoutesItem savedRoutesItem) {
        if(savedRoutesItem == null){
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            return;
        }
        if(isDelete){
            List<Route> collect = savedRoutesItem.getRoutes().stream()
                    .filter(route1 -> !route1.getRoute_id().equals(routeToBeDeleted.getRoute_id()))
                    .collect(Collectors.toList());
            DeleteOfflineDataAsync deleteOfflineDataAsync = new DeleteOfflineDataAsync(file,savedRouteView);
            deleteOfflineDataAsync.execute(new SavedRoutesItem(collect,savedRoutesItem.getMyPlaces()));
        }else{
            savedRouteView.setRouteList(savedRoutesItem.getRoutes(),savedRoutesItem.getMyPlaces());
        }
        if(progressDialog != null){
            progressDialog.dismiss();
        }
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
