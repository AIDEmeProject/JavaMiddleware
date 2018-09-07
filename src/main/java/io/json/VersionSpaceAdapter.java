package io.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.KernelVersionSpace;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.LinearKernel;
import utils.linprog.LinearProgramSolver;

import java.lang.reflect.Type;

class VersionSpaceAdapter implements com.google.gson.JsonDeserializer<VersionSpace> {
    @Override
    public VersionSpace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean addIntercept = jsonObject.getAsJsonPrimitive("addIntercept").getAsBoolean();

        String solver = jsonObject.getAsJsonPrimitive("solver").getAsString();
        LinearProgramSolver.FACTORY factory = LinearProgramSolver.getFactory(getSolverLibrary(solver));

        HitAndRunSampler hitAndRunSampler = jsonDeserializationContext.deserialize(jsonObject.get("hitAndRunSampler"), HitAndRunSampler.class);

        Kernel kernel = jsonDeserializationContext.deserialize(jsonObject.get("kernel"), Kernel.class);

        LinearVersionSpace linearVersionSpace = new LinearVersionSpace(hitAndRunSampler, factory);
        if (addIntercept){
            linearVersionSpace.addIntercept();
        }

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
