package com.squadro.touricity.view.map.DirectionsAPI;

import com.google.android.gms.maps.model.LatLng;

public class DirectionPost {


    public String getDirectionsURL(LatLng origin, LatLng dest, LatLng[] waypoints, String mode) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String str_mode = "mode=" + mode;

        String str_waypoints = "";

        if(waypoints != null && waypoints.length > 0){
            str_waypoints += "waypoints=";

            for(int i=0 ; i<waypoints.length ; i++){
                str_waypoints += waypoints[i].latitude + "," + waypoints[i].longitude + "|";
            }
            str_waypoints = str_waypoints.substring(0, str_waypoints.length()-1);
        }

        // Building the parameters to the web service
        String parameters = str_mode + "&" + str_origin + "&" + str_dest + "&" + sensor + "&" + str_waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";

        //url = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=walking&origin=37.757946%2C39.4048&destination=37.757954%2C38.426349&waypoints=37.746560%2C38.408328&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";
        return url;
    }
}
