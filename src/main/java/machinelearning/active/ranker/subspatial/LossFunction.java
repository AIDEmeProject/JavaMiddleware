package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public interface LossFunction {
    static LossFunction fromStringId(String id) {
        switch (id.toUpperCase()) {
            case "L1":
                return new L1LossFunction();
            case "L2":
                return new L2LossFunction();
            case "PROD":
                return new ProdLossFunction();
            case "ENTROPY":
                return new EntropyLossFunction();
            default:
                throw new RuntimeException("Unknown loss function: " + id.toUpperCase());
        }
    }

    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return final informative score (lower scores are more informative)
     */
    Vector apply(Vector[] subspatialProbabilities);
}
