package data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndexedDatasetTest {
    private List<Long> indexes;
    private Matrix data;
    private IndexedDataset dataset;

    @BeforeEach
    void setUp() {
        indexes = Arrays.asList(0L, 10L, 20L);
        data = Matrix.FACTORY.make(3, 2, 1, 2, 3, 4, 5, 6);  // [[1,2], [3,4], [5, 6]]
        dataset = new IndexedDataset(indexes, data);
    }

    @Test
    void build_noDataPointsAdded_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void build_pointsOfDifferentDimensionsAdded_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0L, new double[1]);
        builder.add(1L, new double[2]);
        assertThrows(RuntimeException.class, builder::build);
    }

    @Test
    void build_compatiblePoints_expectedDatasetConstructed() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0L, new double[] {1, 2});
        builder.add(10L, new double[] {3, 4});
        builder.add(20L, new double[] {5, 6});
        assertEquals(dataset, builder.build());
    }

    @Test
    void constructor_indexesAndMatrixHaveDifferentSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new IndexedDataset(Arrays.asList(1L, 2L), data));
    }

    @Test
    void length_datasetContainsThreeDataPoints_returnsThree() {
        assertEquals(data.rows(), dataset.length());
    }

    @Test
    void dim_dataPointsAreTwoDimensional_returnsTwo() {
        assertEquals(data.cols(), dataset.dim());
    }

    @Test
    void get_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.get(-1));
    }

    @Test
    void get_indexEqualsToLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.get(dataset.length()));
    }

    @Test
    void get_indexInBounds_returnsExpectedDataPoint() {
        assertEquals(new DataPoint(0L, Vector.FACTORY.make(1, 2)), dataset.get(0));
        assertEquals(new DataPoint(10L, Vector.FACTORY.make(3, 4)), dataset.get(1));
        assertEquals(new DataPoint(20L, Vector.FACTORY.make(5, 6)), dataset.get(2));
    }

    @Test
    void getRows_emptyIndexesArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getRows());
    }

    @Test
    void getRows_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRows(-1));
    }

    @Test
    void getRows_indexEqualsToLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRows(dataset.length()));
    }

    @Test
    void getRows_indexInBounds_returnsExpectedIndexedDataset() {
        IndexedDataset expected = new IndexedDataset(Arrays.asList(20L, 10L), Matrix.FACTORY.make(2, 2, 5, 6, 3, 4));
        assertEquals(expected, dataset.getRows(2, 1));
    }

    @Test
    void getCols_emptyIndexesArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getCols());
    }

    @Test
    void getCols_negativeIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getCols(-1));
    }

    @Test
    void getCols_indexEqualsToDimension_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getCols(dataset.dim()));
    }

    @Test
    void getCols_indexInBounds_returnsExpectedIndexedDataset() {
        IndexedDataset expected = new IndexedDataset(indexes, Matrix.FACTORY.make(3, 1, 2, 4, 6));
        assertEquals(expected, dataset.getCols(1));
    }


    @Test
    void getRange_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRange(-1, 0));
    }

    @Test
    void getRange_indexLargerThanLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRows(0, dataset.length()+1));
    }

    @Test
    void getRange_indexInBounds_returnsExpectedIndexedDataset() {
        IndexedDataset expected = new IndexedDataset(Arrays.asList(10L, 20L), Matrix.FACTORY.make(2, 2, 3, 4, 5, 6));
        assertEquals(expected, dataset.getRange(1, 3));
    }

    @Test
    void swap_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(0, -1));
    }

    @Test
    void swap_indexEqualsToLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(dataset.length(), 0));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(0, dataset.length()));
    }

    @Test
    void swap_identicalIndexes_datasetRemainsUnchanged() {
        IndexedDataset copy = dataset.copy();
        dataset.swap(0, 0);
        assertEquals(copy, dataset);
    }

    @Test
    void swap_differentIndexesInRange_datasetRowsCorrectlySwapped() {
        IndexedDataset expected = new IndexedDataset(Arrays.asList(10L, 0L, 20L), Matrix.FACTORY.make(3, 2,  3, 4, 1, 2, 5, 6));
        dataset.swap(0, 1);
        assertEquals(expected, dataset);
    }

    @Test
    void sample_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.sample(-1));
    }

    @Test
    void sample_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.sample(0));
    }

    @Test
    void sample_sampleSizeEqualsLength_returnsTheOwnDataset() {
        assertSame(dataset, dataset.sample(dataset.length()));
    }

    @Test
    void sample_sampleSizeSmallerThanLength_returnsDatasetOfExpectedSize() {
        assertEquals(2, dataset.sample(2).length());
    }

    @Test
    void copy_swapCopyRows_originalDatasetRemainsUnchanged() {
        IndexedDataset copy = dataset.copy();
        dataset.copy().swap(0, 1);
        assertEquals(copy, dataset);
    }

    @Test
    void copyWithSameIndexes_newMatrixHasDifferentLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.copyWithSameIndexes(Matrix.FACTORY.zeros(1, 2)));
    }

    @Test
    void copyWithSameIndexes_compatibleMatrix_returnsDatasetWithNewDataButSameIndexes() {
        Matrix matrix = Matrix.FACTORY.make(3, 1, 10, 11, 12);
        IndexedDataset newDataset = dataset.copyWithSameIndexes(matrix);
        assertEquals(indexes, newDataset.getIndexes());
        assertEquals(matrix, newDataset.getData());
    }
}