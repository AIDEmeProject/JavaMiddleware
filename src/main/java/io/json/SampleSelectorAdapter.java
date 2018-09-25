package io.json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.convexbody.sampling.selector.SampleSelector;

class SampleSelectorAdapter extends JsonDeserializedAdapter<SampleSelector> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.active.learning.versionspace.convexbody.sampling.selector";
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
