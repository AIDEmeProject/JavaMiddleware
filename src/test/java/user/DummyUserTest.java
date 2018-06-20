package user;

import data.DataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DummyUserTest {
    private Collection<DataPoint> points;
    private DummyUser user;

    @BeforeEach
    void setUp() {
        points = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            points.add(new DataPoint(i, new double[]{i+1}));
        }

        Set<Long> positiveKeys = new HashSet<>();
        positiveKeys.add(1L);
        positiveKeys.add(2L);

        user = new DummyUser(positiveKeys);
    }

    @Test
    void constructor_emptyKeySet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new DummyUser(new HashSet<>()));
    }

    @Test
    void getLabel_testAllIndexes_returnsCorrectLabels() {
        int i = 0;
        for (DataPoint point : points) {
            int label = (i == 1 || i == 2) ? 1 : 0;
            assertEquals(label, user.getLabel(point));
            i++;
        }
    }

    @Test
    void getAllLabels_callGetAllLabels_returnsLabelsArray() {
        assertArrayEquals(new int[] {0,1,1,0}, user.getLabel(points));
    }

}