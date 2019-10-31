package machinelearning.active.learning.versionspace.manifold.sphere;


import machinelearning.active.learning.versionspace.manifold.Geodesic;
import machinelearning.active.learning.versionspace.manifold.GeodesicTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreatCircleTest extends GeodesicTest {
    @BeforeEach
    void setUp() {
        center = Vector.FACTORY.make(1, 0);
        direction = Vector.FACTORY.make(0, 1);
        geodesic = getInstance(center, direction);
    }

    @Override
    public Geodesic getInstance(Vector center, Vector direction) {
        return new GreatCircle(center, direction);
    }

    @Test
    void constructor_CenterAndDirectionAreNotOrthogonal_ThrowsException() {
        assertThrows(RuntimeException.class, () -> getInstance(center, center));
    }

    @Test
    @Override
    public void getPoint_PositionDifferentFromZero_CorrectPointIsReturned() {
        assertEquals(Vector.FACTORY.make(0, 1), geodesic.getPoint(Math.PI/2));
        assertEquals(Vector.FACTORY.make(0, -1), geodesic.getPoint(-Math.PI/2));
        assertEquals(Vector.FACTORY.make(-1, 0), geodesic.getPoint(Math.PI));
        assertEquals(Vector.FACTORY.make(-1, 0), geodesic.getPoint(-Math.PI));
    }
}