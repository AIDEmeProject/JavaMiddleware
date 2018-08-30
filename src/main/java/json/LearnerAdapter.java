package json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.classifier.Learner;

public class LearnerAdapter extends JsonDeserializedAdapter<Learner> {
    @Override
    public String getPackagePrefix() {
        return "machinelearner.classifier" ;
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
