package data.preprocessing;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardScalerTest {
    private ArrayList<DataPoint> points;
    private StandardScaler scaler;

    @BeforeEach
    void setUp() {
        double[][] X = new double[][]{{-1, -2}, {0, 1}, {1, 2}};

        computePointsFromMatrix(X);

        scaler = StandardScaler.fit(points);
    }

    private void computePointsFromMatrix(double[][] X){
        points = new ArrayList<>(X.length);
        for (int i = 0; i < X.length; i++){
            points.add(new DataPoint(i, X[i]));
        }
    }

    @Test
    void fit_emptyInputCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> StandardScaler.fit(new ArrayList<>()));
    }

    @Test
    void fit_columnOfZeroStandardDeviation_throwsException() {
        computePointsFromMatrix(new double[][] {{1}, {1}, {1}});
        assertThrows(IllegalArgumentException.class, () -> StandardScaler.fit(points));
    }

    @Test
    void fit_inputContainRowsOfDifferentDimensions_throwsException() {
        computePointsFromMatrix(new double[][] {{1, 2}, {3}});
        assertThrows(IllegalArgumentException.class, () -> StandardScaler.fit(points));
    }

    @Test
    void transform_emptyInputCollection_returnsEmptyCollection() {
        assertTrue(scaler.transform(Collections.EMPTY_LIST).isEmpty());
    }

    @Test
    void transform_inputHasIncompatibleDimension_throwsException() {
        DataPoint point = new DataPoint(0, new double[] {1});
        List<DataPoint> toTransform = new ArrayList<>();
        toTransform.add(point);
        assertThrows(RuntimeException.class, () -> scaler.transform(toTransform));
    }

    @Test
    void transform_validInput_inputCorrectlyNormalized() {
        double[][] expected = new double[][] {
                {-1. , -1.120897076},
                { 0. ,  0.320256307},
                { 1. ,  0.800640769}
        };

        Collection<DataPoint> scaled = scaler.transform(points);

        int i = 0;
        for (DataPoint point : scaled) {
            assertArrayEquals(expected[i++], point.getData(), 1e-8);
        }
    }
}