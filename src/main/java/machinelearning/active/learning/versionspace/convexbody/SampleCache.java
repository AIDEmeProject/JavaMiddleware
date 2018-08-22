package machinelearning.active.learning.versionspace.convexbody;

import utils.linalg.LinearAlgebra;

/**
 * The SampleCache is a module for caching samples from the {@link machinelearning.active.learning.versionspace.LinearVersionSpace}
 * object. One of the inherent problems with the LinearVersionSpace sampling algorithm is in the computation of a first
 * initial sample inside the Version Space, which requires solving a Linear Programming problem. In the Active Learning scenario,
 * we can alleviate this problem through a caching procedure: every time the sampling algorithm is called, we cache the samples
 * drawn from the version space. In the next call of the sampling procedure, we check whether one of the previous samples is
 * already inside the current version space, avoid the Linear Programming step most of time. This method works well in
 * practice since the Version Space does not tend to reduce abruptly from on iteration to another.
 */
public class SampleCache {
    /**
     * cache samples
     */
    private double[][] cachedSamples;

    /**
     * Initialize object with a empty cache
     */
    public SampleCache() {
        cachedSamples = new double[0][];
    }

    /**
     * Replace current cache with a new sample
     *
     * @param samples new sample to cache
     */
    public void updateCache(double[][] samples) {
        cachedSamples = samples;
    }

    /**
     * This method checks whether any one of the cached samples are inside the input {@link ConvexBody}. If so, we return
     * a "wrapped convex body", which returns the cached sample whenever the getInteriorPoint() method is called. Otherwise,
     * the input object is returned without modification.
     *
     * @param convexBody: a convex body object
     * @return the wrapped convex body
     */
    public ConvexBody attemptToSetDefaultInteriorPoint(ConvexBody convexBody) {
        for (double[] cachedSample : cachedSamples){
            // TODO: make this line testable / push this dependency logic to KernelVersionSpace somehow ?
            cachedSample = LinearAlgebra.truncateOrPaddleWithZeros(cachedSample, convexBody.getDim());

            if (convexBody.isInside(cachedSample)) {
                return new ConvexBodyWrapper(convexBody, cachedSample);
            }
        }

        return convexBody;
    }

    private static class ConvexBodyWrapper implements ConvexBody {
        private ConvexBody convexBody;
        private double[] interiorPointCache;

        public ConvexBodyWrapper(ConvexBody convexBody, double[] interiorPointCache) {
            this.convexBody = convexBody;
            this.interiorPointCache = interiorPointCache;
        }

        @Override
        public int getDim() {
            return convexBody.getDim();
        }

        @Override
        public boolean isInside(double[] x) {
            return convexBody.isInside(x);
        }

        @Override
        public double[] getInteriorPoint() {
            return interiorPointCache;
        }

        @Override
        public LineSegment computeLineIntersection(Line line) {
            return convexBody.computeLineIntersection(line);
        }
    }
}