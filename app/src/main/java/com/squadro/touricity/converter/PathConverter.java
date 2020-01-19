package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.converter.interfaces.IConverter;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Vertex;

import java.util.ArrayList;
import java.util.List;

public class PathConverter implements IConverter {

    public Object jsonToObject(JsonObject json) {

        String path_id = json.get("path_id").getAsString();
        int duration = json.get("duration").getAsInt();
        int expense = json.get("expense").getAsInt();
        String comment = json.get("comment").getAsString();
        int path_type = json.get("path_type").getAsInt();
        String vertices = json.get("vertices").getAsString();
        List<Vertex> vertex_list = stringToVertexList(vertices);

        return new Path(null, expense, duration, comment, path_id, path_type, vertex_list);
    }

    public JsonObject objectToJson(Object object) {

        JsonObject json = new JsonObject();
        Path path = (Path) object;

        String vertex_str = vertexListToString(path.getVertices());

        json.addProperty("path_id", path.getPath_id());
        json.addProperty("duration", path.getDuration());
        json.addProperty("expense", path.getExpense());
        json.addProperty("comment", path.getComment());
        json.addProperty("path_type", path.getPath_type());
        json.addProperty("vertices", vertex_str);

        return json;
    }

    public List<Vertex> stringToVertexList(String vertices){

        List<Vertex> vertex_list = new ArrayList<>();

        //TODO: implement stringToVertexList

        return vertex_list;
    }

    public String vertexListToString(List<Vertex> vertexList){

        String vertex_str = "";

        //TODO: implement vertexListToString

        return vertex_str;
    }

}
