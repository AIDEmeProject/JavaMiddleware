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

package utils;

import java.util.function.Function;

/**
 * Utility class for finding the minimum / maximum values of a function over a collection.
 */
public class LinearSearch {
    /**
     *  Finds the minimizer of a function over a collection.
     * @param collection: collection of elements to find minimizer
     * @param function: score function returning a double value for each
     * @return an object containing both the minimum function value and the an element achieving the minimum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> T findMinimizer(Iterable<T> collection, Function<T, Double> function){
        T minimizer = null;
        Double minimum = Double.POSITIVE_INFINITY;

        for (T element : collection) {
            Double value = function.apply(element);

            if (value < minimum) {
                minimum = value;
                minimizer = element;
            }
        }

        if (minimizer == null) {
            throw new IllegalArgumentException("Empty input.");
        }

        return minimizer;
    }

    /**
     * Finds the maximizer of a function over a collection.
     * @param collection: collection of elements to find maximizer
     * @param function: score function returning a double value for each
     * @return an object containing both the minimum function value and the an element achieving the maximum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> T findMaximizer(Iterable<T> collection, Function<T, Double> function){
        return findMinimizer(collection, x -> -function.apply(x));
    }
}
