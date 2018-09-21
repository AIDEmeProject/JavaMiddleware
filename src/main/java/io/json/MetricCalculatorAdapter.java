package io.json;

import exceptions.UnknownClassIdentifierException;
import explore.metrics.MetricCalculator;

public class MetricCalculatorAdapter extends JsonDeserializedAdapter<MetricCalculator> {
    @Override
    public String getPackagePrefix() {
        return "explore.metrics";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()) {
            case "CONFUSIONMATRIX":
                return "ConfusionMatrixCalculator";
            case "THREESETMETRIC":
                return "ThreeSetMetricCalculator";
            default:
                throw new UnknownClassIdentifierException("MetricCalculator", identifier);
        }
    }
}
