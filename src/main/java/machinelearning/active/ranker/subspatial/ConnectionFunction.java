package machinelearning.active.ranker.subspatial;

import utils.linalg.Vector;

public interface ConnectionFunction {
    static ConnectionFunction fromStringId(String id) {
        switch (id.toUpperCase()) {
            case "L1":
                return new L1ConnectionFunction();
            case "L2":
                return new L2ConnectionFunction();
            case "PROD":
                return new ProdConnectionFunction();
            case "ENTROPY":
                return new EntropyConnectionFunction();
            default:
                throw new RuntimeException("Unknown connection function: " + id.toUpperCase());
        }
    }

    /**
     * @param subspatialProbabilities positive class probabilities {p_1, ..., p_k} for each subspace
     * @return final informative score (lower scores are more informative)
     */
    Vector apply(Vector[] subspatialProbabilities);
}
