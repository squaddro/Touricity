package com.squadro.touricity.view.map.DirectionsAPI;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;


public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    @Getter
    private List<LatLng> pointList;
    //public IAsync delegate = null;

    public IAsync2 async2;

    @Getter
    public int seconds;

    public ParserTask(IAsync2 async2){
        this.async2 = async2;
        this.pointList = new ArrayList<>();
    }


    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            DataParser parser = new DataParser();

        // Starts parsing data
            routes = parser.parse(jObject);
            this.seconds = parser.getSeconds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }
    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        if(result == null) return;
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
        // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
        // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }// Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);
        }
        // Drawing polyline in the Google Map for the i-th route
        if(lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            this.pointList = lineOptions.getPoints();
            async2.onComplete2(this.pointList, getSeconds());
        }
        else {

        }
    }
}