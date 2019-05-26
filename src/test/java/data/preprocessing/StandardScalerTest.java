package data.preprocessing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardScalerTest {
    private Matrix points;
    private StandardScaler scaler;

    @BeforeEach
    void setUp() {
        points = Matrix.FACTORY.make(3, 2, -1, -2, 0, 1, 1, 2);
        scaler = StandardScaler.fit(points);
    }

    @Test
    void fit_columnOfZeroStandardDeviation_throwsException() {
        points = Matrix.FACTORY.make(3, 1, 1, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> StandardScaler.fit(points));
    }

    @Test
    void transform_inputHasIncompatibleDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> scaler.transform(Matrix.FACTORY.zeros(1, 1)));
    }

    @Test
    void transform_validInput_inputCorrectlyNormalized() {
        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(scaler.transform(points), 1e-8));
    }
}