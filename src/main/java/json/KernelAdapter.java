package json;

import exceptions.UnknownClassIdentifierException;
import machinelearning.classifier.svm.Kernel;

class KernelAdapter extends JsonDeserializedAdapter<Kernel> {
    @Override
    public String getPackagePrefix() {
        return "machinelearning.classifier.svm";
    }

    @Override
    public String getCanonicalName(String identifier) {
        switch (identifier.toUpperCase()){
            case "LINEAR":
                return "LinearKernel";
            case "GAUSSIAN":
                return "GaussianKernel";
            default:
                throw new UnknownClassIdentifierException("Kernel", identifier);
        }
    }
}
