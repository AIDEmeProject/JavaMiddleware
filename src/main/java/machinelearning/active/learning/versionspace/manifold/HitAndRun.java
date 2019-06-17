package machinelearning.active.learning.versionspace.manifold;

import utils.RandomState;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;
import java.util.Random;

public class HitAndRun {

    private final ConvexBody body;
//    private final DirectionSampler sampler;

//    public HitAndRun(ConvexBody body, DirectionSampler sampler) {
    public HitAndRun(ConvexBody body) {
        this.body = Objects.requireNonNull(body);
//        this.sampler = Objects.requireNonNull(sampler);
    }

    public Chain newChain() {
        return new Chain(body, RandomState.newInstance());
//        return new Chain(body, sampler, RandomState.newInstance());
    }

    public static class Chain {
        private final ConvexBody body;
//        private final DirectionSampler sampler;
        private final Random random;
        private Vector currentSample;

//        Chain(ConvexBody body, DirectionSampler sampler, Random random) {
        Chain(ConvexBody body, Random random) {
            this.body = body;
//            this.sampler = sampler;
            this.random = random;
            this.currentSample = body.getInteriorPoint();
        }

        public Vector advance() {
//            Vector randomDirection = sampler.sampleDirection(currentSample, random);
//            Geodesic geodesic = body.getManifold().getGeodesic(currentSample, randomDirection);
            Geodesic geodesic = body.getManifold().sampleGeodesic(currentSample, random);
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
