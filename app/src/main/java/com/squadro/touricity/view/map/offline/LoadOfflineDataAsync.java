package com.squadro.touricity.view.map.offline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ProgressBar;

import com.squadro.touricity.MainActivity;
import com.squadro.touricity.R;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.MapFragmentTab3;
import com.squadro.touricity.view.map.placesAPI.MyPlace;
import com.squadro.touricity.view.routeList.MyPlaceSave;
import com.squadro.touricity.view.routeList.RoutePlace;
import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadOfflineDataAsync extends AsyncTask<Void, Integer, RoutePlace> {

    private final File file;
    private Context context;
    private SavedRouteView savedRouteView;
    private ProgressBar progressBar;
    private XStream xStream;

    public LoadOfflineDataAsync(SavedRouteView savedRouteView, File file, Context context) {
        this.savedRouteView = savedRouteView;
        this.file = file;
        this.context = context;
        xStream = new XStream();
        progressBar = MapFragmentTab3.rootView.findViewById(R.id.progressBarLoad);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected RoutePlace doInBackground(Void... voids) {
        if (file.length() == 0) return null;
        SavedRoutesItem savedRoutes = getSavedRoutes(file);
        List<Route> routes = null;
        List<MyPlaceSave> myPlaces = null;
        List<MyPlace> places = new ArrayList<>();
        if (savedRoutes != null) {
            routes = savedRoutes.getRoutes();
            myPlaces = savedRoutes.getMyPlaces();
        }
        int count = 0;
        if (myPlaces != null) {
            for (MyPlaceSave myPlaceSave : myPlaces) {
                List<String> photosIds = myPlaceSave.getPhotosIds();
                if (photosIds != null) {
                    count += photosIds.size();
                }
            }
        }
        progressBar.setMax(count+20);
        if (myPlaces != null) {
            for (MyPlaceSave myPlaceSave : myPlaces) {
                List<Bitmap> bitmapList = new ArrayList<>();
                for (String id : myPlaceSave.getPhotosIds()) {
                    File root = new File(context.getFilesDir(), "PlacePhotos");
                    if (!root.exists()) {
                        root.mkdir();
                    }
                    File file = new File(root, id + ".png");
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    bitmapList.add(bitmap);
                    publishProgress(progressBar.getProgress() + 1);
                }
                MyPlace myPlace = new MyPlace(myPlaceSave, bitmapList);
                places.add(myPlace);
                if (MainActivity.checkConnection()) {
                    if (!MapFragmentTab2.isPlaceExist(myPlace)){
                        MapFragmentTab2.responsePlaces.add(myPlace);
                        MapFragmentTab3.responsePlaces.add(myPlace);
                    }
                } else {
                    if (!MapFragmentTab3.isPlaceExist(myPlace))
                        MapFragmentTab3.responsePlaces.add(myPlace);
                }
            }
        }
        MapFragmentTab3.savedRoutesItem = new SavedRoutesItem(routes, myPlaces);
        return new RoutePlace(routes, places);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(RoutePlace routePlace) {
        if (routePlace != null) {
            savedRouteView.setRouteList(routePlace.getRoutes(), routePlace.getMyPlaces());
        }
        progressBar.setProgress(progressBar.getMax());
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
