package io.json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.classifier.Learner;

class LearnerAdapter extends JsonDeserializedAdapter<Learner> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.classifier" ;
    }

    public String getCanonicalName(String identifier){
        switch (identifier.toUpperCase()) {
            case "SVM":
                return "svm.SvmLearner";
            case "KNN":
                return "neighbors.NearestNeighborsLearner";
            case "MAJORITYVOTE":
                return "MajorityVoteLearner";
            default:
                throw new UnknownClassIdentifierException("Learner ", identifier);
        }
    }
}
