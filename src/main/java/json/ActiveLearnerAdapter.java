package json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.active.ActiveLearner;

public class ActiveLearnerAdapter extends JsonDeserializedAdapter<ActiveLearner> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.active.learning";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()){
            case "RANDOM":
                return "RandomSampler";
            case "UNCERTAINTY":
                return "UncertaintySampler";
            case "SIMPLEMARGIN":
                return "SimpleMargin";
            case "GBS":
                return "GeneralizedBinarySearch";
            default:
                throw new UnknownClassIdentifierException("ActiveLearner", identifier);
        }
    }
}
