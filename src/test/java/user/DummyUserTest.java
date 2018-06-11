package user;

import data.LabeledData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DummyUserTest {
    private LabeledData data;
    private int[] labels;
    private DummyUser user;

    @BeforeEach
    void setUp() {
        data = new LabeledData(new double[][]{{1}, {2}, {3}, {4}});
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
}