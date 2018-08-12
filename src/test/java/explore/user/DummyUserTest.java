package explore.user;

import data.DataPoint;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.PortableServer.POA;

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
        Label[] labels = new Label[] {Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE};
        int i = 0;
        for (DataPoint point : points) {
            assertEquals(labels[i++], user.getLabel(point));
        }
    }

    @Test
    void getAllLabels_callGetAllLabels_returnsLabelsArray() {
        assertArrayEquals(new Label[] {Label.NEGATIVE,Label.POSITIVE,Label.POSITIVE,Label.NEGATIVE}, user.getLabel(points));
    }

}