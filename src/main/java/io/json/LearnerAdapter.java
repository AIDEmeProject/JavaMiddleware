package io.json;

import com.google.gson.*;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.classifier.CategoricalLearner;
import machinelearning.classifier.Learner;
import machinelearning.classifier.MajorityVoteLearner;
import machinelearning.classifier.SubspatialLearner;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

import java.lang.reflect.Type;

public class LearnerAdapter implements JsonDeserializer<Learner> {
    @Override
    public Learner deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String identifier = jsonObject.get("name").getAsString().toUpperCase();

        switch (identifier) {
            case "SVM":
                double C = jsonObject.get("C").getAsDouble();
                Kernel kernel = jsonDeserializationContext.deserialize(jsonObject.get("kernel"), Kernel.class);
                return new SvmLearner(C, kernel);

            case "MAJORITYVOTE":
                int sampleSize = jsonObject.get("sampleSize").getAsInt();
                VersionSpace versionSpace = jsonDeserializationContext.deserialize(jsonObject.get("versionSpace"), VersionSpace.class);
                return new MajorityVoteLearner(versionSpace, sampleSize);
            case "SUBSPATIALLEARNER":
                Learner[] learners;

                if (jsonObject.has("repeat")) {
                    int repeat = jsonObject.get("repeat").getAsInt();

                    learners = new Learner[repeat];
                    for (int i = 0; i < repeat; i++) {
                        learners[i] = jsonDeserializationContext.deserialize(jsonObject.get("subspaceLearners"), Learner.class);
                    }
                } else {
                    learners = jsonDeserializationContext.deserialize(jsonObject.get("subspaceLearners"), Learner[].class);
                }

                int[] categoricalIndexes = jsonObject.has("categorical") ? convertJsonArray(jsonObject.get("categorical").getAsJsonArray()) : new int[0];
                for (int index : categoricalIndexes) {
                    learners[index] = new CategoricalLearner();
                }

                return new SubspatialLearner(learners);
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
