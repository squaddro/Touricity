package com.squadro.touricity.view.map.offline;

import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownloadMapTiles {
    private String baseUrl = "https://mt.google.com/vt/lyrs=m";

    public DownloadMapTiles() {
    }

    public Map<String, String> downloadTileBounds(LatLngBounds latLngBounds) {
        Map<String, String> totalUrls = new HashMap<>();
        for (int zoom = 5; zoom <= 18; zoom++) {
            double n = Math.pow(2, zoom);
            double xMin = latLngBounds.southwest.longitude;
            double xMax = latLngBounds.northeast.longitude;
            double yMax = latLngBounds.southwest.latitude;
            double yMin = latLngBounds.northeast.latitude;

            int tileXMin = (int) (n / (2 * Math.PI) * (xMin * Math.PI / 180 + Math.PI));
            int tileYMin = (int) (n / (2 * Math.PI) * (Math.PI - Math.log(Math.tan(Math.PI / 4 + yMin * Math.PI / 360))));
            int tileXMax = (int) (n / (2 * Math.PI) * (xMax * Math.PI / 180 + Math.PI));
            int tileYMax = (int) (n / (2 * Math.PI) * (Math.PI - Math.log(Math.tan(Math.PI / 4 + yMax * Math.PI / 360))));

            Map<String, String> urls = getUrls(zoom, tileXMin, tileYMin, tileXMax, tileYMax);
            if (urls != null) {
                totalUrls.putAll(urls);
            }
        }
        return totalUrls;
    }

    private Map<String, String> getUrls(int zoom, int tileXMin, int tileYMin, int tileXMax, int tileYMax) {
        Map<String, String> urlAndFileName = new HashMap<>();
        for (int tileX = tileXMin; tileX <= tileXMax; tileX++) {
            for (int tileY = tileYMin; tileY <= tileYMax; tileY++) {
                String downloadFileName = zoom + "_" + tileX + "_" + tileY + ".png";
                String urlStr = baseUrl.concat("&x=" + tileX + "&y=" + tileY + "&z=" + zoom);
                urlAndFileName.put(urlStr, downloadFileName);
            }
        }
        return urlAndFileName;
    }

    public void download(Map<String, String> urlsAndFileNames, ProgressBar progressBar, int gap) {
        File apkStorage = new File(
                Environment.getExternalStorageDirectory() + "/"
                        + "Tiles");

        if (!apkStorage.exists()) {
            apkStorage.mkdir();
        }

        for (Map.Entry<String, String> entry : urlsAndFileNames.entrySet()) {
            String urlStr = entry.getKey();
            String fileName = entry.getValue();
            File outputFile = new File(apkStorage, fileName);
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                progressBar.setProgress(progressBar.getProgress() + gap);
                continue;
            }
            URL url = null;
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                progressBar.setProgress(progressBar.getProgress() + gap);
                e.printStackTrace();
            }
            HttpURLConnection c = null;
            InputStream is = null;
            try {
                if (url != null) {
                    c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    is = c.getInputStream();
                }
            } catch (Exception e) {
                progressBar.setProgress(progressBar.getProgress() + gap);
                e.printStackTrace();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                fos.close();
                is.close();
                progressBar.setProgress(progressBar.getProgress() + gap);
            } catch (Exception e) {
                progressBar.setProgress(progressBar.getProgress() + gap);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}
