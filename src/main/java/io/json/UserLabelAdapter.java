/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
