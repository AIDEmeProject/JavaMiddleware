package user;

import data.LabeledDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DummyUserTest {
    private LabeledDataset data;
    private int[] labels;
    private DummyUser user;

    @BeforeEach
    void setUp() {
        data = new LabeledDataset(new double[][]{{1}, {2}, {3}, {4}});
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
    void getLabel_rowIndexOutOfBounds_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> user.getLabel(data, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> user.getLabel(data, labels.length));
    }

    @Test
    void getLabel_rowIndexIsValid_returnsCorrectLabels() {
        for (int i = 0; i < labels.length; i++) {
            assertEquals(labels[i], user.getLabel(data, i));
        }
    }

    @Test
    void getAllLabels_callGetAllLabels_returnsLabelsArray() {
        assertArrayEquals(labels, user.getAllLabels(data));
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
        assertArrayEquals(labels, user.getAllLabels(data));
    }
}