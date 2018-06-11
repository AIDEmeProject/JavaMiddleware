package preprocessing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StandardScalerTest {
    private double[][] X;
    private StandardScaler scaler;

    @BeforeEach
    void setUp() {
        X = new double[][] {{-1,-2}, {0,1},{1,2}};
        scaler = new StandardScaler();
        scaler.fit(X);
    }

    @Test
    void fit_zeroLengthInputArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> scaler.fit(new double[][] {}));
    }


    @Test
    void fit_zeroDimensionInputArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> scaler.fit(new double[][] {{}}));
    }

    @Test
    void fit_constantColumn_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> scaler.fit(new double[][] {{1, 1, 1}}));
    }

    @Test
    void transform_zeroLengthInputArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> scaler.transform(new double[][] {}));
    }


    @Test
    void transform_zeroDimensionInputArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> scaler.transform(new double[][] {{}}));
    }

    @Test
    void transform_inputHasIncompatibleDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> scaler.transform(new double[][]{{1}}));
    }

    @Test
    void transform_notFit_throwsException() {
        scaler = new StandardScaler();
        assertThrows(RuntimeException.class, () -> scaler.transform(X));
    }

    @Test
    void transform_validInput_correctValueReturned() {
        double[][] expected = new double[][] {
                {-1.22474487 , -1.37281294},
                { 0.         , 0.392232270},
                { 1.22474487 , 0.980580675}
        };
        double[][] scaled = scaler.transform(X);
        assertArrayEquals(expected[0], scaled[0], 1e-8);
        assertArrayEquals(expected[1], scaled[1], 1e-8);
        assertArrayEquals(expected[2], scaled[2], 1e-8);
    }

    @Test
    void isFit_objectNotFitted_returnsFalse() {
        scaler = new StandardScaler();
        assertFalse(scaler.isFit());
    }

    @Test
    void isFit_objectFitted_returnsTrue() {
        assertTrue(scaler.isFit());
    }
}