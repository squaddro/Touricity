package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.message.types.Location;
import com.squadro.touricity.message.types.Stop;

public class StopConverter extends AbstractEntryConverter<Stop> {

    protected Stop jsonToEntry(JsonObject json) {
        Location location = new Location();

        String stop_id = "";
        String location_id;
        try {
            stop_id = json.get("stop_id").getAsString();
            location_id = json.get("location_id").getAsString();
            location.setLocation_id(location_id);
        } catch (Exception e) {
        }

        return new Stop(null, 0, 0, null, location, stop_id);
    }

    protected JsonObject entryToJson(Object object) {

        JsonObject json = new JsonObject();
        Stop stop = (Stop) object;

        json.addProperty("stop_id", stop.getStop_id());
        json.addProperty("duration", stop.getDuration());
        json.addProperty("expense", stop.getExpense());
        json.addProperty("comment", stop.getComment());
        json.addProperty("location_id", stop.getLocation_id());

        return json;
    }
}
