package io.json;

import com.google.gson.*;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.*;
import machinelearning.active.ranker.subspatial.LossFunction;
import machinelearning.classifier.CategoricalLearner;
import machinelearning.classifier.FakeCategoricalLearner;
import machinelearning.classifier.Learner;
import machinelearning.classifier.SubspatialLearner;
import machinelearning.classifier.svm.FakeSvmLearner;
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
                for (int index : categoricalIndexes) {
                    learners[index] = new CategoricalLearner();
                }

                String connectionFunctionId = jsonObject.get("lossFunctionId").getAsString();

                if (connectionFunctionId.toUpperCase().equals("MARGIN")) {
                    for (int i = 0; i < learners.length; i++) {
                        if (learners[i] instanceof SvmLearner)
                            learners[i] = new FakeSvmLearner((SvmLearner) learners[i]);
                        else if (learners[i] instanceof CategoricalLearner)
                            learners[i] = new FakeCategoricalLearner();
                        else
                            throw new RuntimeException("MARGIN loss function can only accept SvmLearners");
                    }
                }

                return new SubspatialActiveLearner(new SubspatialLearner(learners), LossFunction.fromStringId(connectionFunctionId));

            case "QUERYBYDISAGREEMENT":
                learner = jsonDeserializationContext.deserialize(jsonObject.get("learner"), Learner.class);
                int backgroundSampleSize = jsonObject.get("backgroundSampleSize").getAsInt();
                double backgroundSamplesWeight = jsonObject.get("backgroundSamplesWeight").getAsDouble();
                return new QueryByDisagreement(learner, backgroundSampleSize, backgroundSamplesWeight);

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
