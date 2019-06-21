package machinelearning.active.learning.versionspace.manifold.euclidean;


import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicTest;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LineTest extends GeodesicTest {
    @Override
    public Geodesic getInstance(Vector center, Vector direction) {
        return new Line(center, direction);
    }

    @Test
    @Override
    public void getPoint_PositionDifferentFromZero_CorrectPointIsReturned() {
        assertEquals(Vector.FACTORY.make(1, 0), geodesic.getPoint(2.));
        assertEquals(Vector.FACTORY.make(5, -4), geodesic.getPoint(-2.));
    }

}