package machinelearning.active.learning.versionspace;

import data.LabeledPoint;
import machinelearning.classifier.Classifier;

import java.util.Collection;

/**
 * Mathematically, the Version Space corresponds as a collection of all classifiers in a particular hypothesis class which
 * are consistent with the labeled data. In order to implement our Version Space-based Active Learning algorithm, we require
 * an algorithm for sampling consistent hypothesis.
 *
 * @see machinelearning.classifier.MajorityVoteLearner
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
