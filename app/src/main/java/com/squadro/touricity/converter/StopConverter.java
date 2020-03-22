package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Stop;

public class StopConverter extends AbstractEntryConverter<Stop> {

    protected Stop jsonToEntry(JsonObject json) {

        JsonObject loc = json.getAsJsonObject("location");
        double lat = loc.get("latitude").getAsDouble();
        double lon = loc.get("longitude").getAsDouble();
        String location_id = loc.get("location_id").getAsString();
        String stop_id = "";
        try {
            stop_id = json.get("stop_id").getAsString();
        } catch (Exception e) {
        }

        return new Stop(null, 0, 0, null, new Location(location_id,lat,lon), stop_id);
    }

    protected JsonObject entryToJson(Object object) {

        JsonObject json = new JsonObject();
        Stop stop = (Stop) object;

        json.addProperty("stop_id", stop.getStop_id());
        json.addProperty("duration", stop.getDuration());
        json.addProperty("expense", stop.getExpense());
        json.addProperty("comment", stop.getComment());

        Location location = stop.getLocation();
        JsonObject locJsonObj = new JsonObject();
        locJsonObj.addProperty("location_id", location.getLocation_id());
        locJsonObj.addProperty("latitude", location.getLatitude());
        locJsonObj.addProperty("longitude", location.getLongitude());
        json.add("location", locJsonObj);

        return json;
    }
}
