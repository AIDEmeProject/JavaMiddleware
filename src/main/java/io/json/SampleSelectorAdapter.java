package io.json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;

class SampleSelectorAdapter extends JsonDeserializedAdapter<SampleSelector> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.active.learning.versionspace.manifold.selector";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()) {
            case "WARMUPANDTHIN":
                return "WarmUpAndThinSelector";
            case "INDEPENDENTCHAINS":
                return "IndependentChainsSelector";
            default:
                throw new UnknownClassIdentifierException("SampleSelector", identifier);
        }
    }
}
