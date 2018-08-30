package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.active.learning.versionspace.convexbody.sampling.selector.SampleSelector;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.Kernel;

public class JsonDeserializer {
    public static <T> void deserialize(String json, Class<T> tClass) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Kernel.class, new KernelAdapter());
        builder.registerTypeAdapter(Learner.class, new LearnerAdapter());
        builder.registerTypeAdapter(SampleSelector.class, new SampleSelectorAdapter());
        builder.registerTypeAdapter(HitAndRunSampler.class, new HitAndRunSamplerAdapter());
        builder.registerTypeAdapter(VersionSpace.class, new LinearVersionSpaceAdapter());
        builder.registerTypeAdapter(ActiveLearner.class, new ActiveLearnerAdapter());

        Gson gson = builder.create();

        System.out.println(gson.fromJson(json, tClass));
    }
}
