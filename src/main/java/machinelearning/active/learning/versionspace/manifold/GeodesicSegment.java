package machinelearning.active.learning.versionspace.manifold;

import utils.Validator;
import utils.linalg.Vector;

public class GeodesicSegment {
    private final double lowerBound, upperBound;
    private final Geodesic geodesic;

    GeodesicSegment(Geodesic geodesic, double lowerBound, double upperBound) {
        Validator.assertIsFinite(lowerBound);
        Validator.assertIsFinite(upperBound);

        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be smaller than upper bound.");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.geodesic = geodesic;
    }

    public final double getLowerBound() {
        return lowerBound;
    }

    public final double getUpperBound() {
        return upperBound;
    }

    public Vector getPoint(double proportion) {
        Validator.assertInRange(proportion, 0, 1);
        return geodesic.getPoint(lowerBound + proportion * (upperBound - lowerBound));
    }

    public GeodesicSegment intersect(GeodesicSegment segment) {
        Validator.assertEquals(geodesic, segment.geodesic);

        return new GeodesicSegment(
                geodesic,
                Math.max(lowerBound, segment.lowerBound),
                Math.min(upperBound, segment.upperBound)
        );
    }
}
