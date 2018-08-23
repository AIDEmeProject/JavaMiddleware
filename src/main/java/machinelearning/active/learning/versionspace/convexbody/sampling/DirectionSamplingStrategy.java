package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;

import java.util.Random;

public interface DirectionSamplingStrategy {
    void fit(ConvexBody body);
    double[] sampleDirection(Random rand);
}
