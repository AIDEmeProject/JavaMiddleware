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

package explore.sampling;

import utils.RandomState;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Class implementing the Reservoir Sampling algorithm, used for uniformly sampling from an array of unknown size.
 * In our particular case, we want so sample from an array whose elements may be "filtered" by a function
 *
 * @see <a href="https://en.wikipedia.org/wiki/Reservoir_sampling">Wikipedia page</a>
 * @author luciano
 */
public class ReservoirSampler {

    /**
     * Extracts a random sample from a collection. We can also specify a filter function for ignoring certain elements.
     * @param collection: collection of elements
     * @param sampleSize: sample size
     * @param filter: predicate which filters any element returning true
     * @return random index
     * @throws IllegalArgumentException if collection is empty
     * @throws IllegalArgumentException if sampleSize is not positive
     * @throws IllegalArgumentException if sample size is larger than the filtered collection's size
     */
    public static <T> Collection<T> sample(Collection<T> collection, int sampleSize, Predicate<T> filter){
        Validator.assertNotEmpty(collection);
        Validator.assertPositive(sampleSize);

        if (sampleSize >= collection.size()) {
            return collection;
        }

        Random random = RandomState.newInstance();
        int index = 0;
        ArrayList<T> result = new ArrayList<>(sampleSize);

        for (T elem : collection) {
            if (filter.test(elem)){
                continue;
            }

            if (index < sampleSize){
                result.add(elem);
            }
            else {
                int j = random.nextInt(index+1);

                if (j < sampleSize) {
                    result.set(j, elem);
                }
            }

            index++;
        }

        if (index < sampleSize){
            throw new IllegalArgumentException("There are less than " + sampleSize + " elements in collection after filtering.");
        }

        return result;
    }

    /**
     * Extracts a random sample from a collection.
     * @param collection: collection to sample from
     * @param sampleSize: random subset size
     * @return a sample from the collection
     * @throws IllegalArgumentException if length is not positive
     * @throws IllegalArgumentException sample size is larger than collection
     */
    public static <T> Collection<T> sample(Collection<T> collection, int sampleSize){
        return sample(collection, sampleSize, pt -> false);
    }

    /**
     * Sample one single element from a collection
     * @param collection: collection of elements
     * @return random index
     * @throws IllegalArgumentException if collection is empty
     */
    public static <T> T sample(Collection<T> collection){
        return sample(collection, 1).iterator().next();
    }
}
