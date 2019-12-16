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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

abstract class JsonDeserializedAdapter<T> implements com.google.gson.JsonDeserializer<T> {
    private static String PROP_NAME ="name";

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        String identifier = json.getAsJsonObject().getAsJsonPrimitive(PROP_NAME).getAsString();
        String classPath = getPackagePrefix() + "." + getCanonicalName(identifier);

        try {
            Class<T> cls = (Class<T>) Class.forName(classPath);
            return (T) context.deserialize(json, cls);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find class: " + classPath);
        }
    }

    public abstract String getPackagePrefix();

    public abstract String getCanonicalName(String identifier);
}
