package data.preprocessing;

import data.IndexedDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardScalerTest {
    private Matrix points;
    private IndexedDataset dataset;
    private StandardScaler scaler;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(10, new double[] {-1, -2});
        builder.add(20, new double[] { 0, 1});
        builder.add(30, new double[] {1, 2});
        dataset = builder.build();
        points = dataset.getData();
        scaler = StandardScaler.fit(points);
    }

    @Test
    void fit_columnOfZeroStandardDeviation_throwsException() {
        points = Matrix.FACTORY.make(3, 1, 1, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> StandardScaler.fit(points));
    }

    @Test
    void transform_validInput_inputCorrectlyNormalized() {
        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(scaler.transform(points), 1e-8));
    }

    @Test
    void transform_indexedDataset_correctOutput() {
        IndexedDataset transformed = scaler.transform(dataset);
        assertEquals(transformed.getIndexes(), dataset.getIndexes());

        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(transformed.getData(), 1e-8));
    }

    @Test
    void fitAndTransform_indexedDataset_outputHasSameIndex() {
        IndexedDataset transformed = StandardScaler.fitAndTransform(dataset);
        assertEquals(transformed.getIndexes(), dataset.getIndexes());

        Matrix expected = Matrix.FACTORY.make(3, 2, -1.2247448714 , -1.372812946, 0. ,  0.3922322703, 1.2247448714 ,  0.9805806757);
        assertTrue(expected.equals(transformed.getData(), 1e-8));
    }
}