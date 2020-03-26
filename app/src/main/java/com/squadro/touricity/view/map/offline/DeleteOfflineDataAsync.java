package com.squadro.touricity.view.map.offline;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.view.routeList.SavedRouteView;
import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DeleteOfflineDataAsync extends AsyncTask<SavedRoutesItem,Void, SavedRoutesItem> {

    private File file;
    private SavedRouteView savedRouteView;

    public DeleteOfflineDataAsync(File file, SavedRouteView savedRouteView){
        this.file = file;
        this.savedRouteView = savedRouteView;
    }
    @Override
    protected SavedRoutesItem doInBackground(SavedRoutesItem... savedRoutesItems) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
        try {
            file.createNewFile();
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new XStream().toXML(new SavedRoutesItem(savedRoutesItems[0].getRoutes(), savedRoutesItems[0].getMyPlaces()), fileWriter);
        return savedRoutesItems[0];
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(SavedRoutesItem savedRoutesItem) {
        savedRouteView.setRouteList(savedRoutesItem.getRoutes(), savedRoutesItem.getMyPlaces());
    }
}
