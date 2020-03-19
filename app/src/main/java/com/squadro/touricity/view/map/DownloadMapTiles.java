package com.squadro.touricity.view.map;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLngBounds;

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

    public DownloadMapTiles() {

    }

    public void downloadTiles(LatLngBounds latLngBounds, int zoom) {
        String urlStr = "";

        double n = Math.pow(2, zoom);
        for (double x = latLngBounds.southwest.latitude; x < latLngBounds.northeast.latitude; x = x + 0.01) {
            for (double y = latLngBounds.southwest.longitude; y < latLngBounds.northeast.longitude; y = y + 0.01) {
                int tileX = (int) (x * n / 256);
                int tileY = (int) (y * n / 256);
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
}
