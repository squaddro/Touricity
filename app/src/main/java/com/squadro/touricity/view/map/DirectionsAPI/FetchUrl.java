package com.squadro.touricity.view.map.DirectionsAPI;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class FetchUrl extends AsyncTask<String, Void, String>{

    //private GoogleMap mMap = null;
    private List<LatLng> pointList = null;

    public IAsync async;

    public FetchUrl(IAsync async){
        this.async = async;
    }


    @Override
    protected String doInBackground(String... url) {
        // For storing data from web service
        String data = "";
        try {
        // Fetching the data from web service
            data = downloadUrl(url[0]);
            Log.d("Background Task data", data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }

        async.onComplete(data);
        return data;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //ParserTask parserTask = new ParserTask();
        // Invokes the thread for parsing the JSON data

        //parserTask.delegate = this;
        //parserTask.execute(result);


    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    public List<LatLng> getPointList() {

            System.out.println("a");

        return pointList;
    }
}