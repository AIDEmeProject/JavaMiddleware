package classifier.nearest_neighbors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NearestNeighborsClassifierTest {
    @Test
    void constructor_emptyArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsClassifier(new int[0], new int[0], 0.5));
    }

    @Test
    void constructor_ArraysHaveDifferentSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsClassifier(new int[2], new int[1], 0.5));
    }

    @Test
    void constructor_GammaIsNegative_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsClassifier(new int[2], new int[2], -1));
    }

    @Test
    void constructor_GammaIsLargerThan1_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new NearestNeighborsClassifier(new int[2], new int[2], 2));
    }
}