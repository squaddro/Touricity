package com.squadro.touricity.converter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
        String creator = json.get("creator").getAsString();
        JsonArray entry_list = json.get("entries").getAsJsonArray();

        for (int i = 0; i < entry_list.size(); i++) {

            JsonObject obj = entry_list.get(i).getAsJsonObject();

            if (obj.has("path_id")) { //entry is a path.

                String path_id = obj.get("path_id").getAsString();
                int duration = obj.get("duration").getAsInt();
                int expense = obj.get("expense").getAsInt();
                String comment = obj.get("comment").getAsString();
                String path_type = obj.get("path_type").getAsString();
                JsonArray vertices = obj.get("vertices").getAsJsonArray();
                List<PathVertex> pathVertex_list = jsonArrayToVertexList(vertices);

                Path path = new Path(null, expense, duration, comment, path_id, path_type, pathVertex_list);

                entries.add(path);
            } else { //entry is a stop.

                String stop_id = obj.get("stop_id").getAsString();
                int duration = obj.get("duration").getAsInt();
                int expense = obj.get("expense").getAsInt();
                String comment = obj.get("comment").getAsString();
                String location_id = obj.get("location_id").getAsString();

                Stop stop = new Stop(null, expense, duration, comment, location_id, stop_id);

                entries.add(stop);
            }
        }
        return new Route(route_id, creator, (IEntry[]) entries.toArray(), "ank", "title1", 0);
    }

    public JsonObject objectToJson(Object object) {

        JsonObject json = new JsonObject();
        Route route = (Route) object;

        json.addProperty("route_id", route.getRoute_id());
        json.addProperty("creator", route.getCreator());

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
                obj.addProperty("path_type", path.getPath_type());
                obj.add("vertices", vertexArr);
            } else if (entry instanceof Stop) { //entry is a stop.

                Stop stop = (Stop) entry;

                obj.addProperty("stop_id", stop.getStop_id());
                obj.addProperty("location_id", stop.getLocation_id());
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
