package com.squadro.touricity.view.map;

import android.content.Context;

import java.io.File;

public class CreateOfflineDataDirectory {

    public CreateOfflineDataDirectory(){}

    public File offlineRouteFile(Context context){
        File file = new File(context.getFilesDir(),"OfflineData");
        if(!file.exists()){
            file.mkdir();
        }

        File dataFile = new File(file, "route.xml");

        return dataFile;
    }
}
