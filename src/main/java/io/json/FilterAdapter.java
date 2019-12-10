package io.json;

import application.filtering.CategoricalFilter;
import application.filtering.Filter;
import application.filtering.RangeFilter;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FilterAdapter implements JsonDeserializer<Filter> {
    @Override
    public Filter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String columnName = jsonObject.get("columnName").getAsString();

        if (jsonObject.has("filterValues")) {
            String[] filterValues = jsonDeserializationContext.deserialize(jsonObject.get("filterValues"), String[].class);
            return new CategoricalFilter(columnName, filterValues);
        }

        else if (jsonObject.has("min") || jsonObject.has("max")) {
            RangeFilter filter = new RangeFilter(columnName);

            if (jsonObject.has("min")) {
                filter.setMin(jsonObject.get("min").getAsDouble());
            }

            if (jsonObject.has("max")) {
                filter.setMax(jsonObject.get("max").getAsDouble());
            }

            return filter;
        }

        throw new IllegalArgumentException("Could not parse json into filter: " + jsonElement);
    }
}
