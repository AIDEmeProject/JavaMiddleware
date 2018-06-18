package sampling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservoirSamplerTest {
    private Collection<Integer> values;

    @BeforeEach
    void setUp() {
        values = Arrays.asList(1,2,3,4,5);
    }

    @Test
    void sample_negativeLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(-1, 1, i -> false));
    }

    @Test
    void sample_zeroLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(0, 1, i -> false));
    }

    @Test
    void sample_negativeSubsetSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(1, -1, i -> false));
    }

    @Test
    void sample_zeroSubsetSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(1, 0, i -> false));
    }

    @Test
    void sample_subsetSizeLargerThanLength_throwsException() {
        assertThrows(RuntimeException.class, () -> ReservoirSampler.sample(1, 2, i -> false));
    }

    @Test
    void sample_emptyCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(new ArrayList<Integer>(), 1));
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

    //    @Test
//    void sample_filterIndex0_0IsNeverSampled() {
//        assertEquals(1, ReservoirSampler.sample(2, i -> i==0));
//    }
}