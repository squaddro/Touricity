package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.message.types.Stop;

public class StopConverter extends AbstractEntryConverter<Stop> {

    protected Stop jsonToEntry(JsonObject json) {

        String stop_id = json.get("stop_id").getAsString();
        String location_id = json.get("location_id").getAsString();

        return new Stop(null, 0, 0, null, location_id, stop_id);
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
