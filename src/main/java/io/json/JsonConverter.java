package io.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import data.LabeledPoint;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.active.learning.versionspace.convexbody.sampling.selector.SampleSelector;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.Kernel;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConverter {
    private static final Gson gson = buildGson();

    private static Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Kernel.class, new KernelAdapter());

        builder.registerTypeAdapter(Learner.class, new LearnerAdapter());
        builder.registerTypeAdapter(ActiveLearner.class, new ActiveLearnerAdapter());

        builder.registerTypeAdapter(SampleSelector.class, new SampleSelectorAdapter());
        builder.registerTypeAdapter(HitAndRunSampler.class, new HitAndRunSamplerAdapter());

        builder.registerTypeAdapter(VersionSpace.class, new VersionSpaceAdapter());

        return builder.create();
    }

    public static <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T deserialize(Reader reader, Class type) {
        return gson.fromJson(new JsonReader(reader), type);
    }

    public static List<LabeledPoint> deserializeLabeledPoints(String json) {
        Type listType = new TypeToken<ArrayList<LabeledPoint>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public static Map<String, Double> deserializeMetricsMap(String json) {
        Type mapType = new TypeToken<HashMap<String,Double>>(){}.getType();
        return gson.fromJson(json, mapType);
    }

    public static <T> String serialize(T value) {
        return gson.toJson(value);
    }
}
