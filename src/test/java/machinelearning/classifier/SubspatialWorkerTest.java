package machinelearning.classifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubspatialWorkerTest {
    private List<Callable<Integer>> jobs;
    private SubspatialWorker worker;

    @BeforeEach
    void setUp() {
        worker = new SubspatialWorker();
    }

    @Test
    void constructor_negativeNumThreads_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SubspatialWorker(-1));
    }

    @Test
    void constructor_zeroNumThreads_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SubspatialWorker(0));
    }

    @Test
    void run_emptyJobs_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> worker.run(new ArrayList<>()));
    }

    @Test
    void run_dummyJobs_expectedResultsComputed() {
        jobs = Arrays.asList(() -> 0, () -> 1, () -> 2);
        assertEquals(Arrays.asList(0, 1, 2), worker.run(jobs));
    }
}