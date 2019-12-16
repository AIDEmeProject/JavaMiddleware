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

import java.util.Random;

/**
 * This class is a factory of Random objects. We create Random instances in a "controlled" fashion, which allows us to
 * make experiments reproducible. All classes should avoid creating Random instances by themselves, and instead call
 * newInstance() method below.
 *
 * Although having such a global state class is generally a bad practice, it is the choice that involves the least amount
 * of changes to the current codebase.
 */
public final class RandomState {
    /**
     * Random seed generator
     */
    private final static Random random = new Random();

    /**
     * This method should only be called once per phase (exploration, evaluation)
     * @param seed: new seed for the RNG
     */
    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * @return a new Random instance. Its seed comes from the internal RNG.
     */
    public static Random newInstance() {
        return new Random(random.nextLong());
    }
}
