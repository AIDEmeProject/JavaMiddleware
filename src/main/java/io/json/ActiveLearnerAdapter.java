package io.json;

import com.google.gson.*;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.RandomSampler;
import machinelearning.active.learning.SimpleMargin;
import machinelearning.active.learning.SubspatialActiveLearner;
import machinelearning.active.learning.UncertaintySampler;
import machinelearning.active.ranker.subspatial.ConnectionFunction;
import machinelearning.classifier.CategoricalLearner;
import machinelearning.classifier.Learner;
import machinelearning.classifier.SubspatialLearner;
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
                Learner[] learners;

                if (jsonObject.has("repeat")) {
                    int repeat = jsonObject.get("repeat").getAsInt();

                    learners = new Learner[repeat];
                    for (int i = 0; i < repeat; i++) {
                        learners[i] = jsonDeserializationContext.deserialize(jsonObject.get("learners"), Learner.class);
                    }
                } else {
                    learners = jsonDeserializationContext.deserialize(jsonObject.get("learners"), Learner[].class);
                }

                int[] categoricalIndexes = jsonObject.has("categorical") ? convertJsonArray(jsonObject.get("categorical").getAsJsonArray()) : new int[0];
                for (int index: categoricalIndexes) {
                    learners[index] = new CategoricalLearner();
                }

                String connectionFunctionId = jsonObject.get("connectionFunctionId").getAsString();
                return new SubspatialActiveLearner(new SubspatialLearner(learners), ConnectionFunction.fromStringId(connectionFunctionId));
            default:
                throw new UnknownClassIdentifierException("ActiveLearner", identifier);
        }
    }

    private static int[] convertJsonArray(JsonArray array) {
        int size = array.size();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = array.get(i).getAsInt();
        }
        return values;
    }
}
