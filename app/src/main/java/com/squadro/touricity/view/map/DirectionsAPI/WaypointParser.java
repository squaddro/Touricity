package com.squadro.touricity.view.map.DirectionsAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class WaypointParser {

    public List<Integer> parse(String str){

        List<Integer> waypoint_order = new ArrayList<>();
        JSONArray jRoutes;

        try{
            JSONObject jObject = new JSONObject(str);
            jRoutes = (JSONArray) jObject.get("routes");
            JSONArray array = (JSONArray) ((JSONObject)jRoutes.get(0)).get("waypoint_order");

            for(int i = 0; i<array.length(); i++){
                waypoint_order.add(array.getInt(i));
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return waypoint_order;
    }

}
