package com.squadro.touricity.converter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.squadro.touricity.converter.interfaces.IConverter;
import com.squadro.touricity.message.types.AbstractEntry;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;

import java.util.ArrayList;
import java.util.List;

public class RouteConverter implements IConverter {

    public Object jsonToObject(JsonObject json) {
        ArrayList<AbstractEntry> entries = new ArrayList<>();

        String route_id = json.get("route_id").getAsString();
        String creator = "";
        String title = json.get("title").getAsString();;
        String city_id = "";
        int privacy = 0;

        JsonArray entry_list = json.get("entries").getAsJsonArray();

        for (int i = 0; i < entry_list.size(); i++) {

            JsonObject obj = entry_list.get(i).getAsJsonObject();
            Log.d("RouteCnvrt", "" + i + " " + obj);

            if (obj == null)
                continue;

            if (obj.has("path_id")) { //entry is a path.

                Path path = (Path) new PathConverter().jsonToObject(obj);

                entries.add(path);
            } else { //entry is a stop.
                Stop stop = (Stop) new StopConverter().jsonToObject(obj);

                entries.add(stop);
            }
        }
        IEntry[] iEntries = new IEntry[entries.size()];
        return new Route(route_id, creator, entries.toArray(iEntries), city_id, title, privacy);
    }

    public JsonObject objectToJson(Object object) {

        JsonObject json = new JsonObject();
        Route route = (Route) object;

        json.addProperty("route_id", route.getRoute_id());
        json.addProperty("creator", route.getCreator());
        json.addProperty("title", route.getTitle());
        json.addProperty("privacy", route.getPrivacy());

        JsonArray entry_list = new JsonArray();
        List<IEntry> entries = route.getAbstractEntryList();

        for (int i = 0; i < entries.size(); i++) {

            IEntry entry = entries.get(i);
            JsonObject obj = new JsonObject();

            obj.addProperty("duration", entry.getDuration());
            obj.addProperty("expense", entry.getExpense());
            obj.addProperty("comment", entry.getComment());

            if (entry instanceof Path) { //entry is a path.

                Path path = (Path) entry;

                JsonArray vertexArr = vertexListToJsonArray(path.getVertices());

                obj.addProperty("path_id", path.getPath_id());
                obj.addProperty("path_type", path.getPath_type().getValue());
                obj.add("vertices", vertexArr);
            } else if (entry instanceof Stop) { //entry is a stop.

                Stop stop = (Stop) entry;

                obj.addProperty("stop_id", stop.getStop_id());
                JsonObject jsonObjectLoc = new JsonObject();
                jsonObjectLoc.addProperty("location_id", stop.getLocation().getLocation_id());
                jsonObjectLoc.addProperty("latitude", stop.getLocation().getLatitude());
                jsonObjectLoc.addProperty("longitude", stop.getLocation().getLongitude());
                obj.add("location", jsonObjectLoc);
            }
            entry_list.add(obj);
        }
        json.add("entries", entry_list);

        return json;
    }

    public List<PathVertex> jsonArrayToVertexList(JsonArray vertices) {

        List<PathVertex> pathVertex_list = new ArrayList<>();

        Gson gson = new Gson();
        for (int i = 0; i < vertices.size(); i++) {
            PathVertex pathVertex = gson.fromJson(vertices.get(i).toString(), PathVertex.class);
            pathVertex_list.add(pathVertex);
        }
        return pathVertex_list;
    }

    public JsonArray vertexListToJsonArray(List<PathVertex> pathVertexList) {
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();
        for (PathVertex pathVertex : pathVertexList) {
            jsonArray.add(gson.toJson(pathVertex));
        }
        return jsonArray;
    }
}
