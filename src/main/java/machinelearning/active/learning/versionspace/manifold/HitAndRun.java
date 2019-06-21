package machinelearning.active.learning.versionspace.manifold;

import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import utils.RandomState;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;
import java.util.Random;

public final class HitAndRun {

    private final ConvexBody body;
    private final DirectionSampler sampler;

    public HitAndRun(ConvexBody body, DirectionSampler sampler) {
        this.body = Objects.requireNonNull(body);
        this.sampler = Objects.requireNonNull(sampler);
    }

    public Chain newChain() {
        return new Chain(body, sampler, RandomState.newInstance());
    }

    public final static class Chain {
        private final ConvexBody body;
        private final DirectionSampler sampler;
        private final Random random;
        private Vector currentSample;

        Chain(ConvexBody body, DirectionSampler sampler, Random random) {
            this.body = body;
            this.sampler = sampler;
            this.random = random;
            this.currentSample = body.getInteriorPoint();
        }

        public Vector advance() {
            Vector randomDirection = sampler.sampleDirection(currentSample, random);
            Geodesic geodesic = body.getManifold().getGeodesic(currentSample, randomDirection);
            GeodesicSegment segment = body.computeIntersection(geodesic);
            currentSample = segment.getPoint(random.nextDouble());
            return currentSample;
        }

        public Vector advance(int n) {
            Validator.assertPositive(n);
            for (int i = 0; i < n; i++) {
                advance();
            }
            return currentSample;
        }
    }
}
