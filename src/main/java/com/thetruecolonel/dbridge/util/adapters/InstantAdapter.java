package com.thetruecolonel.dbridge.util.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;

public class InstantAdapter implements JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (json == null || json.isJsonNull())
            return null;

        return OffsetDateTime.parse(json.getAsString()).toInstant();
    }
}
