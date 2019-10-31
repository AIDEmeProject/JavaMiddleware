package io.json;

import exceptions.UnknownClassIdentifierException;
import explore.sampling.InitialSampler;

public class InitialSamplerAdapter extends JsonDeserializedAdapter<InitialSampler> {
    @Override
    public String getPackagePrefix() {
        return "explore.sampling";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()) {
            case "STRATIFIEDSAMPLER":
                return "StratifiedSampler";
            case "FIXEDSAMPLER":
                return "FixedSampler";
            default:
                throw new UnknownClassIdentifierException("InitialSampler", identifier);
        }
    }
}
