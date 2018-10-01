package io.json;

import com.google.gson.*;
import explore.user.UserLabel;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;

import java.lang.reflect.Type;

public class UserLabelAdapter implements JsonDeserializer<UserLabel>, JsonSerializer<UserLabel> {
    @Override
    public UserLabel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonArray();
        Label[] labels = new Label[array.size()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = array.get(i).toString().equals("\"POSITIVE\"") ? Label.POSITIVE : Label.NEGATIVE;
        }
        return new LabelGroup(labels);
    }

    @Override
    public JsonElement serialize(UserLabel userLabel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray labelsArray = new JsonArray();

        for (Label label : userLabel.getLabelsForEachSubspace()) {
            labelsArray.add(label.toString());
        }

        return labelsArray;
    }
}
