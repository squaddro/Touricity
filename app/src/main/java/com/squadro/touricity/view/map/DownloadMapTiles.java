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

    public void downloadTileBounds(LatLngBounds latLngBounds) {
        String urlStr = "";

        for (int zoom = 0; zoom <= 18; zoom++) {
            double n = Math.pow(2, zoom);
            double xMin = latLngBounds.southwest.longitude;
            double xMax = latLngBounds.northeast.longitude;
            double yMax = latLngBounds.southwest.latitude;
            double yMin = latLngBounds.northeast.latitude;

            int tileXMin = (int) (n / (2 * Math.PI) * (xMin * Math.PI / 180 + Math.PI));
            int tileYMin = (int) (n / (2 * Math.PI) * (Math.PI - Math.log(Math.tan(Math.PI / 4 + yMin * Math.PI / 360))));
            int tileXMax = (int) (n / (2 * Math.PI) * (xMax * Math.PI / 180 + Math.PI));
            int tileYMax = (int) (n / (2 * Math.PI) * (Math.PI - Math.log(Math.tan(Math.PI / 4 + yMax * Math.PI / 360))));

            download(zoom, tileXMin, tileYMin, tileXMax, tileYMax);
        }
    }

    private void download(int zoom, int tileXMin, int tileYMin, int tileXMax, int tileYMax) {
        String urlStr;
        for (int tileX = tileXMin; tileX <= tileXMax; tileX++) {
            for (int tileY = tileYMin; tileY <= tileYMax; tileY++) {
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
                } else {
                    continue;
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
}
