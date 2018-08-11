package machinelearning.active.learning.versionspace;

import machinelearning.classifier.Classifier;
import data.LabeledPoint;

import java.util.Collection;

/**
 * Mathematically, the Version Space corresponds as a collection of all classifiers in a particular hypothesis class which
 * are consistent with the labeled data. In order to implement our Version Space-based Active Learning algorithm, we require
 * to be able to sample hypothesis from it.
 *
 * @see machinelearning.active.learning.GeneralizedBinarySearch
 */
public interface VersionSpace {
    /**
     * @param labeledPoints: training data
     * @param numSamples: number of hypothesis to sample
     * @return a random sample of numSamples hypothesis consistent with the labeledPoints.
     * @throws IllegalArgumentException if numSamples is not positive
     */
    Classifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples);
}
