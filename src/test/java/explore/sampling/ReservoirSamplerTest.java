package explore.sampling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservoirSamplerTest {
    private List<Integer> values;

    @BeforeEach
    void setUp() {
        values = Arrays.asList(1,2,3,4,5);
    }

    @Test
    void sample_emptyCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(new ArrayList<>()));
    }

    @Test
    void sample_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, -1));
    }

    @Test
    void sample_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, 0));
    }

    @Test
    void sample_collectionSmallerThanSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, values.size()+1));
    }

    @Test
    void sample_collectionSizeEqualsToSampleSize_returnsTheInputCollection() {
        assertEquals(values, ReservoirSampler.sample(values, values.size()));
    }

    @Test
    void sample_filteredCollectionSizeSmallerThanSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, 3, x -> x >= 3));
    }

    @Test
    void sample_filteredAllElementsButOne_ReturnsRemainingElement() {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(1);
        assertEquals(result, ReservoirSampler.sample(values, 1, x -> x >= 2));
    }
}