package io.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;

import java.lang.reflect.Type;

class HitAndRunSamplerAdapter implements com.google.gson.JsonDeserializer<HitAndRunSampler> {
    @Override
    public HitAndRunSampler deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        SampleSelector selector = context.deserialize(jsonObject.get("selector"), SampleSelector.class);

        boolean addCaching = jsonObject.getAsJsonPrimitive("cache").getAsBoolean();
        boolean addRounding = jsonObject.getAsJsonPrimitive("rounding").getAsBoolean();
        //boolean addRoundingCache = jsonObject.getAsJsonPrimitive("roundingCache").getAsBoolean();
        boolean addRoundingCache = false;
        HitAndRunSampler.Builder builder = new HitAndRunSampler.Builder(selector);

        if (addCaching) builder.addSampleCache();

        if (addRoundingCache) builder.addRoundingCache();

        if (addRounding){
            long maxIter = jsonObject.has("maxIter") ? jsonObject.getAsJsonPrimitive("maxIter").getAsLong() : Long.MAX_VALUE;
            builder.addRounding(maxIter);
        }

        return builder.build();
    }
}
