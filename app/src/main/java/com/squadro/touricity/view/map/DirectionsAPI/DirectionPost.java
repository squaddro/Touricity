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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";

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
        if(stopList.size() <= 3){
            return null;
        }

        //Origin of the route
        String str_origin = "origin=" + stopList.get(0).getLocation().getLatitude() + "," + stopList.get(0).getLocation().getLongitude();

        // Destination of route
        String str_dest = "destination=" + (stopList.get(stopList.size()-1)).getLocation().getLatitude() + "," + (stopList.get(stopList.size()-1)).getLocation().getLongitude();

        // Sensor enabled
        String sensor = "sensor=false";

        String str_mode = "mode=" + mode;

        String str_waypoints = "waypoints=optimize=true|";

        int i = 1;
        while (i < stopList.size()-1){

            str_waypoints += stopList.get(i).getLocation().getLatitude() + "," + stopList.get(i).getLocation().getLongitude() + "|";
            i++;
        }
        str_waypoints = str_waypoints.substring(0, str_waypoints.length()-1);

        // Building the parameters to the web service
        String parameters = str_mode + "&" + str_origin + "&" + str_dest + "&" + sensor + "&" + str_waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBrr2iE49aWzGwLhWPYW5ABBV6Ja-8zyvE";

        return url;
    }
}
