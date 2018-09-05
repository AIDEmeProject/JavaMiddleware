package io.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import utils.linprog.LinearProgramSolver;

import java.lang.reflect.Type;

class LinearVersionSpaceAdapter implements com.google.gson.JsonDeserializer<LinearVersionSpace> {
    @Override
    public LinearVersionSpace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        boolean addIntercept = jsonObject.getAsJsonPrimitive("addIntercept").getAsBoolean();

        String solver = jsonObject.getAsJsonPrimitive("solver").getAsString();
        LinearProgramSolver.FACTORY factory = LinearProgramSolver.getFactory(getSolverLibrary(solver));

        HitAndRunSampler hitAndRunSampler = jsonDeserializationContext.deserialize(jsonObject.get("hitAndRunSampler"), HitAndRunSampler.class);

        LinearVersionSpace linearVersionSpace = new LinearVersionSpace(hitAndRunSampler, factory);
        if (addIntercept){
            linearVersionSpace.addIntercept();
        }

        return linearVersionSpace;
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
