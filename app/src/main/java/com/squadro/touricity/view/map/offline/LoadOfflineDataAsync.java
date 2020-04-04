package com.squadro.touricity.view.map.offline;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.MainActivity;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadOfflineDataAsync extends AsyncTask<Void, Void, SavedRoutesItem> {

    private final File file;
    private Context context;
    private SavedRouteView savedRouteView;
    private XStream xStream = null;
    private ProgressDialog progressDialog;

    public LoadOfflineDataAsync(SavedRouteView savedRouteView, File file, Context context) {
        this.savedRouteView = savedRouteView;
        this.file = file;
        this.context = context;
        xStream = new XStream();
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "INFO", "Please wait while the offline data loading...");
        super.onPreExecute();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected SavedRoutesItem doInBackground(Void... voids) {
        if (file.length() == 0) return null;
        SavedRoutesItem savedRoutes = getSavedRoutes(file);
        List<Route> routes = null;
        List<MyPlaceSave> myPlaces = null;
        if (savedRoutes != null) {
            routes = savedRoutes.getRoutes();
            myPlaces = savedRoutes.getMyPlaces();
        }
        for (MyPlaceSave myPlaceSave : myPlaces) {
            List<Bitmap> bitmapList = new ArrayList<>();
            for (byte[] bytes : myPlaceSave.getPhotos()) {
                Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmapList.add(decodedByte);
            }
            MyPlace myPlace = new MyPlace(myPlaceSave, bitmapList);
            if (MainActivity.checkConnection()) {
                if (!MapFragmentTab2.isPlaceExist(myPlace))
                    MapFragmentTab2.responsePlaces.add(myPlace);
            } else {
                if (!MapFragmentTab3.isPlaceExist(myPlace))
                    MapFragmentTab3.responsePlaces.add(myPlace);
            }
        }
        return new SavedRoutesItem(routes, myPlaces);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(SavedRoutesItem savedRoutesItem) {
        if (savedRoutesItem == null) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            return;
        }
        savedRouteView.setRouteList(savedRoutesItem.getRoutes(), savedRoutesItem.getMyPlaces());
        MapFragmentTab3.savedRoutesItem = savedRoutesItem;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
