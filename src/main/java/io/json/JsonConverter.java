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

import application.filtering.Filter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import data.LabeledPoint;
import exceptions.JsonDeserializationFailedException;
import explore.metrics.MetricCalculator;
import explore.sampling.InitialSampler;
import explore.user.UserLabel;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.Kernel;
import machinelearning.threesetmetric.LabelGroup;
import utils.linalg.Vector;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This module is responsible for deserializing JSON strings into java objects, and, conversely, serializing general java
 * objects into JSON strings. More specifically, this class is a wrapper of Google's <a href="https://github.com/google/gson">Gson library</a>,
 * a very well know and widely adopted library for such purposes.
 */
public class JsonConverter {
    private static final Gson gson = buildGson();

    private static Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(UserLabel.class, new UserLabelAdapter());
        builder.registerTypeAdapter(Label.class, new UserLabelAdapter());
        builder.registerTypeAdapter(LabelGroup.class, new UserLabelAdapter());

        builder.registerTypeAdapter(Vector.class, new VectorAdapter());

        builder.registerTypeAdapter(Kernel.class, new KernelAdapter());

        builder.registerTypeAdapter(Learner.class, new LearnerAdapter());
        builder.registerTypeAdapter(ActiveLearner.class, new ActiveLearnerAdapter());

        builder.registerTypeAdapter(SampleSelector.class, new SampleSelectorAdapter());
        builder.registerTypeAdapter(HitAndRunSampler.class, new HitAndRunSamplerAdapter());

        builder.registerTypeAdapter(VersionSpace.class, new VersionSpaceAdapter());

        builder.registerTypeAdapter(MetricCalculator.class, new MetricCalculatorAdapter());

        builder.registerTypeAdapter(InitialSampler.class, new InitialSamplerAdapter());

        builder.registerTypeAdapter(Filter.class, new FilterAdapter());

        return builder.create();
    }

    /**
     * @param json: JSON string representation of a class T object
     * @param type: class type to be deserialized
     * @param <T>: class to deserialize
     * @return an object of the class T deserialized from the JSON string input
     * @throws JsonDeserializationFailedException if the json string failed to be deserialized
     */
    public static <T> T deserialize(String json, Class<T> type) throws JsonDeserializationFailedException {
        try {
            return gson.fromJson(json, type);
        } catch (JsonParseException ex) {
            throw new JsonDeserializationFailedException(type, ex);
        }
    }

    /**
     * @param reader: a {@link Reader} object
     * @param type: class type to be deserialized
     * @param <T>: class to deserialize
     * @return an object of the class T deserialized from the string obtained from the reader
     * @throws JsonDeserializationFailedException if the json string failed to be deserialized
     */
    public static <T> T deserialize(Reader reader, Class<T> type) throws JsonDeserializationFailedException {
        try {
            return gson.fromJson(new JsonReader(reader), type);
        } catch (JsonParseException ex) {
            throw new JsonDeserializationFailedException(type, ex);
        }
    }

    /**
     * @param json: a JSON string encoding a list of {@link LabeledPoint} objects.
     * @return the deserialized list of labeled points
     * @throws JsonDeserializationFailedException if the json string failed to be deserialized
     */
    public static List<LabeledPoint> deserializeLabeledPoints(String json) throws JsonDeserializationFailedException {
        Type listType = new TypeToken<ArrayList<LabeledPoint>>(){}.getType();
        try {
            return gson.fromJson(json, listType);
        } catch (JsonParseException ex) {
            throw new JsonDeserializationFailedException(listType, ex);
        }
    }

    /**
     * @param json: a JSON string on the format {key : value}, where "value" must be a number
     * @return a map of (String, Double) entries interpreted
     * @throws JsonDeserializationFailedException if the json string failed to be deserialized
     */
    public static Map<String, Double> deserializeMetricsMap(String json) throws JsonDeserializationFailedException {
        Type mapType = new TypeToken<HashMap<String,Double>>(){}.getType();
        try {
            return gson.fromJson(json, mapType);
        } catch (JsonParseException ex) {
            throw new JsonDeserializationFailedException(mapType, ex);
        }
    }

    /**
     * @param value: an object to be serialized
     * @return a String serializing the input object
     */
    public static <T> String serialize(T value) {
        return gson.toJson(value);
    }
}
