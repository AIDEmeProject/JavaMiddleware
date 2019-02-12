package data;

import explore.user.UserLabel;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LabeledDatasetTest {
    private List<Long> indexes;
    private Matrix data;
    private UserLabel[] labels;
    private LabeledDataset dataset;

    @BeforeEach
    void setUp() {
        indexes = Arrays.asList(0L, 10L, 20L);
        data = Matrix.FACTORY.make(3, 2, 1, 2, 3, 4, 5, 6);  // [[1,2], [3,4], [5, 6]]
        labels = new UserLabel[]{Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE};
        dataset = new LabeledDataset(indexes, data, labels);
    }

    @Test
    void constructor_indexesHasIncompatibleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(Arrays.asList(1L, 2L), data, labels));
    }

    @Test
    void constructor_matrixHasIncompatibleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(indexes, Matrix.FACTORY.zeros(2, 2), labels));
    }

    @Test
    void constructor_labelsHasIncompatibleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(indexes, data, new Label[] {Label.POSITIVE}));
    }

    @Test
    void constructor_indexedDatasetAndLabelsHaveIncompatibleSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new IndexedDataset(indexes, data), new Label[] {Label.POSITIVE}));
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
    void append_dataAndLabelsHaveDifferentSizes_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        builder.add(30L, new double[]{7, 8});
        assertThrows(IllegalArgumentException.class, () -> dataset.append(builder.build(), new Label[]{Label.POSITIVE, Label.POSITIVE}));

        builder.add(40L, new double[]{9, 10});
        assertThrows(IllegalArgumentException.class, () -> dataset.append(builder.build(), new Label[]{Label.POSITIVE}));
    }

    @Test
    void append_dataOfIncompatibleDimension_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(30L, new double[]{7, 8, 9});

        assertThrows(IllegalArgumentException.class, () -> dataset.append(builder.build(), new Label[]{Label.POSITIVE}));
    }

    @Test
    void append_newLabeledData_returnsNewLabeledSetWithNewDataAppendedToTheEnd() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(30L, new double[]{7, 8});
        builder.add(40L, new double[]{9, 10});

        LabeledDataset appendedData = dataset.append(builder.build(), new Label[]{Label.NEGATIVE, Label.POSITIVE});
        assertEquals(new LabeledPoint(0L, Vector.FACTORY.make(1, 2), Label.POSITIVE), appendedData.get(0));
        assertEquals(new LabeledPoint(10L, Vector.FACTORY.make(3, 4), Label.NEGATIVE), appendedData.get(1));
        assertEquals(new LabeledPoint(20L, Vector.FACTORY.make(5, 6), Label.POSITIVE), appendedData.get(2));
        assertEquals(new LabeledPoint(30L, Vector.FACTORY.make(7, 8), Label.NEGATIVE), appendedData.get(3));
        assertEquals(new LabeledPoint(40L, Vector.FACTORY.make(9, 10), Label.POSITIVE), appendedData.get(4));
    }

    @Test
    void get_indexInBounds_returnsExpectedDataPoint() {
        assertEquals(new LabeledPoint(0L, Vector.FACTORY.make(1, 2), labels[0]), dataset.get(0));
        assertEquals(new LabeledPoint(10L, Vector.FACTORY.make(3, 4), labels[1]), dataset.get(1));
        assertEquals(new LabeledPoint(20L, Vector.FACTORY.make(5, 6), labels[2]), dataset.get(2));
    }

    @Test
    void copyWithSameIndexesAndLabels_newMatrixHasDifferentLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.copyWithSameIndexesAndLabels(Matrix.FACTORY.zeros(1, 2)));
    }

    @Test
    void copyWithSameIndexesAndLabels_compatibleMatrix_returnsDatasetWithNewDataButSameIndexes() {
        Matrix matrix = Matrix.FACTORY.make(3, 1, 10, 11, 12);
        LabeledDataset newDataset = dataset.copyWithSameIndexesAndLabels(matrix);
        assertEquals(indexes, newDataset.getIndexes());
        assertEquals(matrix, newDataset.getData());
        assertArrayEquals(labels, newDataset.getLabels());
    }
}