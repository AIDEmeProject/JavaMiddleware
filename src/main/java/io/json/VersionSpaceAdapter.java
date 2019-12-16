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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.KernelVersionSpace;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.bayesian.BayesianLinearVersionSpace;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.LinearKernel;
import utils.linprog.LinearProgramSolver;

import java.lang.reflect.Type;

class VersionSpaceAdapter implements com.google.gson.JsonDeserializer<VersionSpace> {
    @Override
    public VersionSpace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean addIntercept = jsonObject.getAsJsonPrimitive("addIntercept").getAsBoolean();
        //boolean decompose = jsonObject.getAsJsonPrimitive("decompose").getAsBoolean();
        boolean decompose = false;
        VersionSpace linearVersionSpace;

        if (jsonObject.has("hitAndRunSampler")) {
            String solver = jsonObject.getAsJsonPrimitive("solver").getAsString();
            LinearProgramSolver.FACTORY factory = LinearProgramSolver.getFactory(getSolverLibrary(solver));

            HitAndRunSampler hitAndRunSampler = jsonDeserializationContext.deserialize(jsonObject.get("hitAndRunSampler"), HitAndRunSampler.class);

            linearVersionSpace = new LinearVersionSpace(hitAndRunSampler, factory);

            if (addIntercept) {
                ((LinearVersionSpace) linearVersionSpace).addIntercept();
            }

            if (decompose) {
                ((LinearVersionSpace) linearVersionSpace).useDecomposition();

                double jitter = jsonObject.has("jitter") ? jsonObject.get("jitter").getAsDouble() : 0;
                ((LinearVersionSpace) linearVersionSpace).setJitter(jitter);
            }

            if (jsonObject.has("sphere") && jsonObject.get("sphere").getAsBoolean())
                ((LinearVersionSpace) linearVersionSpace).useSphericalSampling();
        }
        else if (jsonObject.has("bayesianSampler")) {
            JsonObject sampler = jsonObject.get("bayesianSampler").getAsJsonObject();
            int warmup = sampler.get("warmup").getAsInt();
            int thin = sampler.get("thin").getAsInt();
            double sigma = sampler.get("sigma").getAsDouble();
            linearVersionSpace = new BayesianLinearVersionSpace(warmup, thin, sigma, addIntercept);
        }
        else {
            throw new IllegalArgumentException("Unknown version space configuration");
        }

        Kernel kernel = jsonDeserializationContext.deserialize(jsonObject.get("kernel"), Kernel.class);

        if (kernel instanceof LinearKernel) {
            return linearVersionSpace;
        }

        return new KernelVersionSpace(linearVersionSpace, kernel);
    }

    private LinearProgramSolver.LIBRARY getSolverLibrary(String solver) {
        switch (solver.toUpperCase()) {
            case "APACHE":
                return LinearProgramSolver.LIBRARY.APACHE;
            case "OJALGO":
                return LinearProgramSolver.LIBRARY.OJALGO;
            default:
                throw new UnknownClassIdentifierException("LinearProgramSolver.LIBRARY", solver);
        }
    }
}
