package machinelearning.active.learning.versionspace.manifold.cache;


import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicSegment;
import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import utils.linalg.Vector;

import java.util.Arrays;

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
    private Vector[] cachedSamples;

    /**
     * Initialize object with a empty cache
     */
    public SampleCache() {
        cachedSamples = new Vector[0];
    }

    /**
     * Replace current cache with a new sample
     *
     * @param samples new sample to cache
     */
    public void updateCache(Vector[] samples) {
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
        for (Vector cachedSample : cachedSamples){
            // TODO: make this line testable / push this dependency logic to KernelVersionSpace somehow ?
            cachedSample = cachedSample.resize(convexBody.dim());

            if (convexBody.isInside(cachedSample)) {
                return new ConvexBodyWrapper(convexBody, cachedSample);
            }
        }

        return convexBody;
    }

    private static class ConvexBodyWrapper implements ConvexBody {
        private ConvexBody convexBody;
        private Vector interiorPointCache;

        public ConvexBodyWrapper(ConvexBody convexBody, Vector interiorPointCache) {
            this.convexBody = convexBody;
            this.interiorPointCache = interiorPointCache;
        }

        @Override
        public int dim() {
            return convexBody.dim();
        }

        @Override
        public boolean isInside(Vector x) {
            return convexBody.isInside(x);
        }

        @Override
        public Vector getInteriorPoint() {
            return interiorPointCache;
        }

        @Override
        public GeodesicSegment computeIntersection(Geodesic geodesic) {
            return convexBody.computeIntersection(geodesic);
        }

        @Override
        public Manifold getManifold() {
            return convexBody.getManifold();
        }

        @Override
        public boolean attemptToReduceEllipsoid(Ellipsoid ellipsoid) {
            return convexBody.attemptToReduceEllipsoid(ellipsoid);
        }

        @Override
        public Ellipsoid getContainingEllipsoid() {
            return convexBody.getContainingEllipsoid();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleCache that = (SampleCache) o;
        return Arrays.equals(cachedSamples, that.cachedSamples);
    }
}
