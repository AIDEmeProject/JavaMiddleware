package io.json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.active.ActiveLearner;

class ActiveLearnerAdapter extends JsonDeserializedAdapter<ActiveLearner> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.active.learning";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()){
            case "RANDOMSAMPLER":
                return "RandomSampler";
            case "UNCERTAINTYSAMPLER":
                return "UncertaintySampler";
            case "SIMPLEMARGIN":
                return "SimpleMargin";
            case "ACTIVETREESEARCH":
                return "ActiveTreeSearch";
            default:
                throw new UnknownClassIdentifierException("ActiveLearner", identifier);
        }
    }
}
