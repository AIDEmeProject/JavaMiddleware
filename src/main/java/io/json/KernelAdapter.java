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
import machinelearning.classifier.svm.DiagonalGaussianKernel;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.LinearKernel;
import utils.linalg.Vector;

import java.lang.reflect.Type;

class KernelAdapter implements JsonDeserializer<Kernel> {
    @Override
    public Kernel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String kernelName = jsonObject.get("name").getAsString();

        switch (kernelName.toUpperCase()) {
            case "LINEAR":
                return new LinearKernel();

            case "GAUSSIAN":
                double gamma = jsonObject.has("gamma") ? jsonObject.get("gamma").getAsDouble() : 0;
                return gamma == 0 ? new GaussianKernel() : new GaussianKernel(gamma);

            case "DIAGONAL":
                JsonArray array = jsonObject.get("diagonal").getAsJsonArray();
                return new DiagonalGaussianKernel(convertJsonArray(array));

            default:
                throw new UnknownClassIdentifierException("Kernel", kernelName);
        }
    }

    private Vector convertJsonArray(JsonArray array) {
        double[] values = new double[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = array.get(i).getAsDouble();
        }
        return Vector.FACTORY.make(values);
    }

}
