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

public class BayesianLinearVersionSpace implements VersionSpace<LinearClassifier> {
    private final boolean addIntercept;
    private final StanLogisticRegressionSampler sampler;

    public BayesianLinearVersionSpace(int warmup, int thin, double sigma, boolean addIntercept) {
        Validator.assertPositive(warmup);
        Validator.assertPositive(thin);
        Validator.assertPositive(sigma);

        this.addIntercept = addIntercept;
        this.sampler = new StanLogisticRegressionSampler(warmup, thin, sigma);
    }

    @Override
    public MajorityVote<LinearClassifier> sample(LabeledDataset labeledPoints, int numSamples) {
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
