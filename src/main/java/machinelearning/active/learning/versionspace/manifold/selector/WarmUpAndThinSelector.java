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

package machinelearning.active.learning.versionspace.manifold.selector;

import machinelearning.active.learning.versionspace.manifold.HitAndRun;
import utils.Validator;
import utils.linalg.Vector;


/**
 * When selecting samples from a Markov Chain, one has to consider two main problems:
 *
 *      - Mixing time: it may take several iterations to reach the stationary distribution (or close enough)
 *      - Correlation: since each sample depends on the previous one, samples are correlated to each other
 *
 * In order to help countering this effect, two strategies are commonly utilized:
 *
 *  - warm-up: we ignore the first "n" samples from the chain. The remaining samples will have a closer distribution to
 *             the stationary distribution.
 *  - thinning: we only select every n-th sample from the chain. This reduces the correlation between samples.
 */
public class WarmUpAndThinSelector implements SampleSelector {
    /**
     * Number of elements to skip during warm-up phase
     */
    private final int warmUp;

    /**
     * Select every "thin"-th element from the chain
     */
    private final int thin;

    /**
     * @param warmUp: the first "warmup" samples will be ignored. The higher this value, the closest to uniform samples will be.
     * @param thin: after the warmup phase, only retain every "thin" samples. The higher this value, the more independent to each other samples will be.
     * @throws IllegalArgumentException if "warmUp" is negative or "thin" is not positive
     */
    public WarmUpAndThinSelector(int warmUp, int thin) {
        Validator.assertNonNegative(warmUp);
        Validator.assertPositive(thin);

        this.warmUp = warmUp;
        this.thin = thin;
    }

    @Override
    public Vector[] select(HitAndRun hitAndRun, int numSamples) {
        Validator.assertPositive(numSamples);

        HitAndRun.Chain chain = hitAndRun.newChain();

        Vector[] samples = new Vector[numSamples];
        samples[0] = chain.advance(warmUp);

        for (int i = 1; i < numSamples; i++) {
            samples[i] = chain.advance(thin);
        }

        return samples;
    }
}
