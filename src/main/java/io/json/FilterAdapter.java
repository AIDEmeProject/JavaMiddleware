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

import application.filtering.CategoricalFilter;
import application.filtering.Filter;
import application.filtering.RangeFilter;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FilterAdapter implements JsonDeserializer<Filter> {
    @Override
    public Filter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String columnName = jsonObject.get("columnName").getAsString();

        if (jsonObject.has("filterValues")) {
            String[] filterValues = jsonDeserializationContext.deserialize(jsonObject.get("filterValues"), String[].class);
            return new CategoricalFilter(columnName, filterValues);
        }

        else if (jsonObject.has("min") || jsonObject.has("max")) {
            RangeFilter filter = new RangeFilter(columnName);

            if (jsonObject.has("min")) {
                filter.setMin(jsonObject.get("min").getAsDouble());
            }

            if (jsonObject.has("max")) {
                filter.setMax(jsonObject.get("max").getAsDouble());
            }

            return filter;
        }

        throw new IllegalArgumentException("Could not parse json into filter: " + jsonElement);
    }
}
