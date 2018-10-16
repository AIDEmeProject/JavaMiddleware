package machinelearning.bayesian;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.classifier.Label;
import machinelearning.classifier.MajorityVote;
import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Arrays;


/**
 * A Bayesian Version Space maintains a probability distribution over the parameters of a classifier (for example, the weights
 * of a Linear Classifier) instead of maintaining cuts. Such distribution is defined by means of the Bayes rule, and it is
 * fitted over labeled data. An advantage of this method is it supports noisy labeling, in contrast with usual Version
 * Space algorithms.
 *
 * In the particular case of this class, we sample Linear Classifiers through this Bayesian approach.
 *
 * @see StanLogisticRegressionSampler
 */
public class BayesianLinearVersionSpace implements VersionSpace<LinearClassifier> {
    /**
     * Whether to fit the intercept
     */
    private final boolean addIntercept;

    /**
     * Stan sampler
     */
    private final StanLogisticRegressionSampler sampler;

    /**
     * @param warmup: number of initial samples to skip
     * @param thin: only keep every "thin" sample after warm-up phase
     * @param sigma: standard deviation of gaussian prior
     * @param addIntercept: whether to fit intercept
     * @throws IllegalArgumentException if warmup, thin, or sigma are negative
     */
    public BayesianLinearVersionSpace(int warmup, int thin, double sigma, boolean addIntercept) {
        Validator.assertPositive(warmup);
        Validator.assertPositive(thin);
        Validator.assertPositive(sigma);

        this.addIntercept = addIntercept;
        this.sampler = new StanLogisticRegressionSampler(warmup, thin, sigma);
    }

    @Override
    public MajorityVote<LinearClassifier> sample(LabeledDataset labeledPoints, int numSamples) {
        Validator.assertPositive(numSamples);

        Matrix data = labeledPoints.getData();
        if (addIntercept) {
            data = data.addBiasColumn();
        }

        int[] ys = Arrays.stream(labeledPoints.getLabels())
                .mapToInt(Label::asBinary)
                .toArray();

        double[][] samples = sampler.run(numSamples, data.toArray(), ys);

        return new MajorityVote<>(Arrays.stream(samples)
                .map(Vector.FACTORY::make)
                .map(x -> new LinearClassifier(x, addIntercept))
                .toArray(LinearClassifier[]::new)
        );
    }
}
