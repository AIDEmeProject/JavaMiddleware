package data;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IndexedDatasetTest {

    @Test
    void constructor_dataArrayAndIndexesHaveDifferentSizes_throwsException() {
        // indexes
        Set<Long> indexes = new LinkedHashSet<>();
        indexes.add(0L);

        // data
        double[][] data = new double[][] {{0},{1}};

        assertThrows(IllegalArgumentException.class, () -> new IndexedDataset(indexes, data));
    }
}