package com.squadro.touricity.converter.interfaces;

import com.google.gson.JsonObject;

public interface IConverter {

    Object jsonToObject(JsonObject json);
    JsonObject objectToJson(Object object);

}
