package com.squadro.touricity.converter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squadro.touricity.converter.interfaces.IConverter;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;

import java.util.ArrayList;
import java.util.List;

public class PathConverter implements IConverter {

    public Object jsonToObject(JsonObject json) {

        String path_id = json.get("path_id").getAsString();
        int duration = json.get("duration").getAsInt();
        int expense = json.get("expense").getAsInt();
        String comment = json.get("comment").getAsString();
        String path_type = json.get("path_type").getAsString();
        JsonArray vertices = json.get("vertices").getAsJsonArray();
        List<PathVertex> pathVertex_list = jsonArrayToVertexList(vertices);

        return new Path(null, expense, duration, comment, path_id, path_type, pathVertex_list);
    }

    public JsonObject objectToJson(Object object) {

        JsonObject json = new JsonObject();
        Path path = (Path) object;

        JsonArray vertexArr = vertexListToJsonArray(path.getVertices());

        json.addProperty("path_id", path.getPath_id());
        json.addProperty("duration", path.getDuration());
        json.addProperty("expense", path.getExpense());
        json.addProperty("comment", path.getComment());
        json.addProperty("path_type", path.getPath_type());
        json.add("vertices", vertexArr);
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

        JsonArray vertexArr = null;

        String json = new Gson().toJson(pathVertexList);
        vertexArr.add(json);
        return vertexArr;
    }

}
