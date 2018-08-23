package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;

import java.util.Random;

public class UnitSphereSampler implements DirectionSamplingStrategy {
    private int dim;

    @Override
    public void fit(ConvexBody body) {
        this.dim = body.getDim();
    }

    @Override
    public double[] sampleDirection(Random rand) {
        double[] direction = new double[dim];

        for (int i = 0; i < dim; i++) {
            direction[i] = rand.nextGaussian();
        }

        return direction;
    }
}
