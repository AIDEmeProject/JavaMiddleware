package json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.active.learning.versionspace.VersionSpace;

class VersionSpaceAdapter extends JsonDeserializedAdapter<VersionSpace> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.active.learning.versionspace";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()) {
            case "LINEAR":
                return "LinearVersionSpace";
            case "KERNEL":
                return "KernelVersionSpace";
            default:
                throw new UnknownClassIdentifierException("VersionSpace", identifier);
        }
    }
}
