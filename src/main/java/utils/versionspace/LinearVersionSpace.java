package utils.versionspace;

import classifier.Classifier;
import classifier.linear.LinearClassifier;
import data.LabeledPoint;
import sampling.HitAndRunSampler;
import utils.Validator;
import utils.convexbody.ConvexCone;

import java.util.ArrayList;
import java.util.Collection;

public class LinearVersionSpace implements VersionSpace {
    private final boolean addIntercept;
    private final HitAndRunSampler sampler;

    public LinearVersionSpace(HitAndRunSampler sampler, boolean addIntercept) {
        Validator.assertNotNull(sampler);
        this.sampler = sampler;
        this.addIntercept = addIntercept;
    }

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
        for (double[] sampledWeight : sampler.sample(new ConvexCone(labeledPoints), numSamples)) {
            classifiers[i++] = new LinearClassifier(sampledWeight, addIntercept);
        }

        return classifiers;
    }
}

