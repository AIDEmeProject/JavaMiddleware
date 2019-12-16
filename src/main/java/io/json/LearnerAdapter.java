/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package io.json;

import com.google.gson.*;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.classifier.*;
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

                SubspatialWorker worker = jsonObject.has("numThreads") ? new SubspatialWorker(jsonObject.get("numThreads").getAsInt()) : new SubspatialWorker();

                return new SubspatialLearner(learners, worker);
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
