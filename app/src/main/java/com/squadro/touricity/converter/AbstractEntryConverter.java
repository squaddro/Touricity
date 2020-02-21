package com.squadro.touricity.converter;

import com.google.gson.JsonObject;
import com.squadro.touricity.converter.interfaces.IConverter;
import com.squadro.touricity.message.types.AbstractEntry;

public abstract class AbstractEntryConverter <T extends AbstractEntry> implements IConverter {

    public final Object jsonToObject(JsonObject json) {
        AbstractEntry entry = jsonToEntry(json);

        entry.setDuration(json.get("duration").getAsInt());
        entry.setExpense(json.get("expense").getAsInt());
        entry.setComment(json.get("comment").getAsString());

        return entry;
    }

    public final JsonObject objectToJson(Object object) {

        JsonObject json = entryToJson(object);

        AbstractEntry entry = (AbstractEntry) object;

        json.addProperty("duration", entry.getDuration());
        json.addProperty("expense", entry.getExpense());
        json.addProperty("comment", entry.getComment());

        return json;

    }

    protected abstract T jsonToEntry(JsonObject json);
    protected abstract JsonObject entryToJson(Object object);
}
