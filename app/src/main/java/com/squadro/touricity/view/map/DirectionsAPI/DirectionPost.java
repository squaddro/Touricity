package com.squadro.touricity.view.map.DirectionsAPI;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;

import java.util.ArrayList;
import java.util.List;

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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDxyMBvgQv-sgYemlNwyUhtjNn6OSsEFlo";

        //url = "https://maps.googleapis.com/maps/api/directions/json?origin=Adelaide,SA&destination=Adelaide,SA&waypoints=optimize:true|Barossa+Valley,SA|Clare,SA|Connawarra,SA|McLaren+Vale,SA&sensor=false&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";
        return url;
    }

    public String getOptimizedDirectionsURL (Route route, String mode){

        List<IEntry> entryList = route.getAbstractEntryList();
        List<Stop> stopList = new ArrayList<>();

        for (IEntry entry: entryList) {
            if(entry instanceof Stop){
                stopList.add((Stop) entry);
            }
        }

        //Origin of the route
        double originLat = (stopList.get(0)).getLocation().getLatitude();
        double originLon = (stopList.get(0)).getLocation().getLongitude();
        String str_origin = "origin=" + originLat + "," + originLon;

        // Destination of route
        double destLat = (stopList.get(stopList.size()-1)).getLocation().getLatitude();
        double destLon = (stopList.get(stopList.size()-1)).getLocation().getLongitude();

        String str_dest = "destination=" + destLat + "," + destLon;

        // Sensor enabled
        String sensor = "sensor=false";

        String str_mode = "mode=" + mode;

        String str_waypoints = "waypoints=optimize:true|";

        for (Stop s:stopList) {
            str_waypoints += s.getLocation().getLatitude() + "," + s.getLocation().getLongitude() + "|";
        }

        str_waypoints = str_waypoints.substring(0, str_waypoints.length()-1);

        // Building the parameters to the web service
        String parameters = str_mode + "&" + str_origin + "&" + str_dest + "&" + sensor + "&" + str_waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDxyMBvgQv-sgYemlNwyUhtjNn6OSsEFlo";

        return url;
    }
}
