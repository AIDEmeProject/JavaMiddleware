package io.json;

import com.google.gson.*;
import exceptions.UnknownClassIdentifierException;
import machinelearning.classifier.svm.DiagonalGaussianKernel;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.LinearKernel;
import utils.linalg.Vector;

import java.lang.reflect.Type;

class KernelAdapter implements JsonDeserializer<Kernel> {
    @Override
    public Kernel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String kernelName = jsonObject.get("name").getAsString();

        switch (kernelName.toUpperCase()) {
            case "LINEAR":
                return new LinearKernel();

            case "GAUSSIAN":
                if (!jsonObject.has("gamma")) {
                    return new GaussianKernel();
                }

                double gamma = jsonObject.get("gamma").getAsDouble();
                return new GaussianKernel(gamma);

            case "DIAGONAL":
                JsonArray array = jsonObject.get("diagonal").getAsJsonArray();
                return new DiagonalGaussianKernel(convertJsonArray(array));

            default:
                throw new UnknownClassIdentifierException("Kernel", kernelName);
        }
    }

    private Vector convertJsonArray(JsonArray array) {
        double[] values = new double[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = array.get(i).getAsDouble();
        }
        return Vector.FACTORY.make(values);
    }

}
