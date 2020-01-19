package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.message.types.Location;

public class LocationConverter implements IConverter {

    public Object jsonToObject(JsonObject json) {

        String location_id = json.get("location_id").getAsString();
        double latitude = json.get("latitude").getAsDouble();
        double longitude = json.get("longitude").getAsDouble();

        return new Location(location_id, latitude, longitude);
    }

    public JsonObject objectToJson(Object object) {

        JsonObject json = new JsonObject();
        Location location = (Location) object;

        json.addProperty("location_id", location.getLocation_id());
        json.addProperty("latitude", location.getLatitude());
        json.addProperty("longitude", location.getLongitude());

        return json;
    }
}
