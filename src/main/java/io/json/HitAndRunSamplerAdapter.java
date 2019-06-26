package io.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.RoundingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;

import java.lang.reflect.Type;

class HitAndRunSamplerAdapter implements com.google.gson.JsonDeserializer<HitAndRunSampler> {
    @Override
    public HitAndRunSampler deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        boolean useRounding = jsonObject.getAsJsonPrimitive("rounding").getAsBoolean();
        int maxIter = jsonObject.has("maxIter") ? jsonObject.getAsJsonPrimitive("maxIter").getAsInt() : Integer.MAX_VALUE;
        DirectionSamplingAlgorithm direction = deserializeDirectionSampler(useRounding, maxIter);

        SampleSelector selector = context.deserialize(jsonObject.get("selector"), SampleSelector.class);

        boolean addCaching = jsonObject.getAsJsonPrimitive("cache").getAsBoolean();
        SampleCache cache = deserializeSampleCaching(addCaching);

        return new HitAndRunSampler(direction, selector, cache);
    }

    private DirectionSamplingAlgorithm deserializeDirectionSampler(boolean useRounding, int maxIter) {
        return useRounding ? new RoundingAlgorithm(maxIter) : new RandomDirectionAlgorithm();
    }

    private SampleCache deserializeSampleCaching(boolean addCaching) {
        return addCaching ? new SampleCache() : new SampleCacheStub();
    }
}
