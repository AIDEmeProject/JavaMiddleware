package machinelearning.active.learning.versionspace;

import data.LabeledPoint;
import machinelearning.active.learning.versionspace.convexbody.HitAndRunSampler;
import machinelearning.active.learning.versionspace.convexbody.PolyhedralCone;
import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The Version Space for the {@link LinearClassifier}. Mathematically, it can be defined by a set of linear inequalities:
 *
 *      \( y_i (b + \langle w, x_i \rangle) &gt; 0 \)
 *
 * As you can see, this region defines a PolyhedralCone in the euclidean space. In order to sample (b,w) pairs from this
 * region, we use the Hit-and-Run algorithm.
 *
 * @see HitAndRunSampler
 * @see PolyhedralCone
 */
public class LinearVersionSpace implements VersionSpace {
    /**
     * Whether to add intercept to data points
     */
    private final boolean addIntercept;

    /**
     * Hit-and-Run sampler
     */
    private final HitAndRunSampler sampler;

    /**
     * @param sampler: Hit-and-Run sampler instance
     * @param addIntercept: whether to include the bias \(b\) when sampling
     * @throws NullPointerException if sampler is null
     */
    public LinearVersionSpace(HitAndRunSampler sampler, boolean addIntercept) {
        Validator.assertNotNull(sampler);
        this.sampler = sampler;
        this.addIntercept = addIntercept;
    }

    /**
     * @param labeledPoints: training data
     * @param numSamples: number of hypothesis to sample
     * @return sample of Linear Classifiers obtained through the Hit-and-Run algorithm.
     */
    @Override
    public LinearClassifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples) {
        Validator.assertPositive(numSamples);

        if(addIntercept){
            Collection<LabeledPoint> labeledPointsWithBias = new ArrayList<>(labeledPoints.size());
            for (LabeledPoint point : labeledPoints){
                labeledPointsWithBias.add(point.addBias());
            }
            labeledPoints = labeledPointsWithBias;
        }

        LinearClassifier[] classifiers = new LinearClassifier[numSamples];

        int i = 0;
        for (double[] sampledWeight : sampler.sample(new PolyhedralCone(labeledPoints), numSamples)) {
            classifiers[i++] = new LinearClassifier(sampledWeight, addIntercept);
        }

        return classifiers;
    }
}

