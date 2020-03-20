package com.squadro.touricity.view.map;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DownloadMapTiles {
    private String baseUrl = "https://mt.google.com/vt/lyrs=m";

    public DownloadMapTiles() {}

    public void downloadTiles(double x, double y) {
        String urlStr = "";
        for (int zoom = 0; zoom < 21; zoom++) {
            double n = Math.pow(2, zoom);
            int tileX =(int)( n/(2*Math.PI)*(x*Math.PI/180 + Math.PI));
            int tileY = (int)(n/(2*Math.PI)*(Math.PI - Math.log(Math.tan(Math.PI/4 + y*Math.PI/360))));

            File apkStorage = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + "Tiles");

            if (!apkStorage.exists()) {
                apkStorage.mkdir();
            }
            String downloadFileName = zoom + "_" + tileX + "_" + tileY + ".png";
            File outputFile = new File(apkStorage, downloadFileName);
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            urlStr = baseUrl.concat("&x=" + tileX + "&y=" + tileY + "&z=" + zoom);
            URL url = null;
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection c = null;
            try {
                c = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                c.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                c.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = null;//Get OutputStream for NewFile Location
            try {
                fos = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            InputStream is = null;//Get InputStream for connection
            try {
                is = c.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                fos.close();
                is.close();
            } catch (Exception e) {
            }
        }
    }
}
