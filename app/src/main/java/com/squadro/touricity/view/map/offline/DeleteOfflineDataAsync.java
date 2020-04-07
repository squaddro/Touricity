package com.squadro.touricity.view.map.offline;

import android.os.AsyncTask;

import com.squadro.touricity.view.routeList.SavedRoutesItem;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DeleteOfflineDataAsync extends AsyncTask<SavedRoutesItem, Void, Void> {

    private File file;

    public DeleteOfflineDataAsync(File file) {
        this.file = file;
    }

    @Override
    protected Void doInBackground(SavedRoutesItem... savedRoutesItems) {
        try {
            FileWriter fileWriter = null;
            file.delete();
            file.createNewFile();
            fileWriter = new FileWriter(file, true);
            new XStream().toXML(savedRoutesItems[0], fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
