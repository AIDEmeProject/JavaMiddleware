package io.json;

import com.google.gson.*;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.*;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.SvmLearner;

import java.lang.reflect.Type;

public class ActiveLearnerAdapter implements JsonDeserializer<ActiveLearner> {
    @Override
    public ActiveLearner deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String identifier = jsonObject.get("name").getAsString().toUpperCase();

        switch (identifier) {
            case "RANDOMSAMPLER":
                return new RandomSampler();
            case "UNCERTAINTYSAMPLER":
                Learner learner = jsonDeserializationContext.deserialize(jsonObject.get("learner"), Learner.class);
                return new UncertaintySampler(learner);
            case "SIMPLEMARGIN":
                SvmLearner svmLearner = jsonDeserializationContext.deserialize(jsonObject.get("svmLearner"), SvmLearner.class);
                return new SimpleMargin(svmLearner);
            case "SUBSPATIALSAMPLER":
                ActiveLearner[] activeLearners;

                if (jsonObject.has("repeat")) {
                    int repeat = jsonObject.get("repeat").getAsInt();

                    activeLearners = new ActiveLearner[repeat];
                    for (int i = 0; i < repeat; i++) {
                        activeLearners[i] = jsonDeserializationContext.deserialize(jsonObject.get("activeLearners"), ActiveLearner.class);
                    }
                } else {
                    activeLearners = jsonDeserializationContext.deserialize(jsonObject.get("activeLearners"), ActiveLearner[].class);
                }

                return new SubspatialActiveLearner(activeLearners);
            case "QUERYBYDISAGREEMENT":
                learner = jsonDeserializationContext.deserialize(jsonObject.get("learner"), Learner.class);
                int backgroundSampleSize = jsonObject.get("backgroundSampleSize").getAsInt();
                return new QueryByDisagreement(learner, backgroundSampleSize);

            default:
                throw new UnknownClassIdentifierException("ActiveLearner", identifier);
        }
    }
}
