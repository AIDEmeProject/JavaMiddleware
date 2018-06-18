package user;

import data.DataPoint;
import data.LabeledDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DummyUserTest {
    private double[][] X;
    private int[] labels;
    private LabeledDataset data;
    private DummyUser user;

    @BeforeEach
    void setUp() {
        X = new double[][]{{1}, {2}, {3}, {4}};
        data = new LabeledDataset(X);
        labels = new int[] {0,1,1,0};
        user = new DummyUser(labels);
    }

    @Test
    void constructor_emptyLabelsArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new DummyUser(new int[] {}));
    }

    @Test
    void constructor_labelsArrayContainsInvalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new DummyUser(new int[] {0,-1,1,0}));
    }

    @Test
    void constructor_allZerosLabels_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new DummyUser(new int[] {0,0,0,0}));
    }

    @Test
    void constructor_allOnesLabels_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new DummyUser(new int[] {1,1,1,1}));
    }

    @Test
    void getLabel_rowIndexIsValid_returnsCorrectLabels() {
        for (int i = 0; i < labels.length; i++) {
            assertEquals(labels[i], user.getLabel(new DataPoint(i, X[i])));
        }
    }

    @Test
    void getAllLabels_callGetAllLabels_returnsLabelsArray() {
        assertArrayEquals(labels, user.getLabel(data.getAllPoints()));
    }

    @Test
    void getAllLabels_usingIndexesConstructor_returnsCorrectLabels() {
        Collection<Long> indexes = new ArrayList<>();
        indexes.add(0L);
        indexes.add(1L);
        indexes.add(2L);
        indexes.add(3L);

        Set<Long> positiveSetIndexes = new HashSet<>();
        positiveSetIndexes.add(1L);
        positiveSetIndexes.add(2L);

        user = new DummyUser(indexes, positiveSetIndexes);
        assertArrayEquals(labels, user.getLabel(data.getAllPoints()));
    }
}