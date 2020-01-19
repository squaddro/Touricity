package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.converter.interfaces.IConverter;
import com.squadro.touricity.message.types.Stop;

public class StopConverter implements IConverter {

    public Object jsonToObject(JsonObject json) {

        String stop_id = json.get("stop_id").getAsString();
        int duration = json.get("duration").getAsInt();
        int expense = json.get("expense").getAsInt();
        String comment = json.get("comment").getAsString();
        String location_id = json.get("location_id").getAsString();

        return new Stop(null, expense, duration, comment, location_id, stop_id);
    }

    public JsonObject objectToJson(Object object) {

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
