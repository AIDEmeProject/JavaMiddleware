package preprocessing;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class StandardScalerTest {
    private ArrayList<DataPoint> points;
    private StandardScaler scaler;

    @BeforeEach
    void setUp() {
        double[][] X = new double[][]{{-1, -2}, {0, 1}, {1, 2}};

        computePointsFromMatrix(X);

        scaler = new StandardScaler();
        scaler.fit(points);
    }

    private void computePointsFromMatrix(double[][] X){
        points = new ArrayList<>(X.length);
        for (int i = 0; i < X.length; i++){
            points.add(new DataPoint(i, X[i]));
        }
    }

    @Test
    void fit_emptyInputCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> scaler.fit(new ArrayList<>()));
    }

    @Test
    void fit_constantColumn_throwsException() {
        computePointsFromMatrix(new double[][] {{1}, {1}, {1}});
        assertThrows(IllegalArgumentException.class, () -> scaler.fit(points));
    }

    @Test
    void transform_emptyInputCollection_returnsEmptyCollection() {
        assertTrue(scaler.transform(new ArrayList<>()).isEmpty());
    }

    @Test
    void transform_notFit_throwsException() {
        scaler = new StandardScaler();
        assertThrows(RuntimeException.class, () -> scaler.transform(points.get(0)));
    }

    @Test
    void transform_inputHasIncompatibleDimension_throwsException() {
        DataPoint point = new DataPoint(0, new double[] {1});
        assertThrows(RuntimeException.class, () -> scaler.transform(point));
    }

    @Test
    void transform_validInput_correctValueReturned() {
        double[][] expected = new double[][] {
                {-1.22474487 , -1.37281294},
                { 0.         , 0.392232270},
                { 1.22474487 , 0.980580675}
        };
        Collection<DataPoint> scaled = scaler.transform(points);

        int i = 0;
        for (DataPoint point : scaled) {
            assertArrayEquals(expected[i++], point.getData(), 1e-8);
        }
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