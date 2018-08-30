package json;

import com.google.gson.*;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.active.learning.versionspace.convexbody.sampling.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.convexbody.sampling.cache.SampleCache;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.RoundingAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.selector.SampleSelector;

import java.lang.reflect.Type;

public class HitAndRunSamplerAdapter implements com.google.gson.JsonDeserializer<HitAndRunSampler> {
    @Override
    public HitAndRunSampler deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        boolean useRounding = jsonObject.getAsJsonPrimitive("rounding").getAsBoolean();
        DirectionSamplingAlgorithm direction = deserializeDirectionSampler(useRounding);

        SampleSelector selector = context.deserialize(jsonObject.get("selector"), SampleSelector.class);

        boolean addCaching = jsonObject.getAsJsonPrimitive("cache").getAsBoolean();
        SampleCache cache = deserializeSampleCaching(addCaching);

        return new HitAndRunSampler.Builder(direction, selector).cache(cache).build();
    }

    private DirectionSamplingAlgorithm deserializeDirectionSampler(boolean useRounding) {
        return useRounding ? new RoundingAlgorithm() : new RandomDirectionAlgorithm();
    }

    private SampleCache deserializeSampleCaching(boolean addCaching) {
        return addCaching ? new SampleCache() : new SampleCacheStub();
    }
}
