package machinelearning.classifier;

import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Executes a list of tasks concurrently
 */
public class SubspatialWorker {

    /**
     * Maximum number of threads available in PC
     */
    private final static int MAX_THREADS = Runtime.getRuntime().availableProcessors() - 1;

    /**
     * Maximum number of threads to use when running tasks
     */
    private final int numThreads;

    /**
     * @param numThreads: maximum number of threads to be used when running tasks
     */
    public SubspatialWorker(int numThreads) {
        Validator.assertPositive(numThreads);
        this.numThreads = numThreads;
    }

    /**
     * Use all available threads when running tasks
     */
    public SubspatialWorker() {
        this(MAX_THREADS);
    }

    /**
     * @param workers: list of tasks to be run
     * @return a list containing the results of each running task, in the same order as input
     */
    public <T> List<T> run(List<Callable<T>> workers) {
        Validator.assertNotEmpty(workers);

        try {
            ExecutorService executor = Executors.newFixedThreadPool(getNumWorkers(workers.size()));
            List<Future<T>> scores = executor.invokeAll(workers);
            executor.shutdown();

            List<T> values = new ArrayList<>(workers.size());

            for (int i=0; i < workers.size(); i++) {
                values.add(scores.get(i).get());
            }

            return values;

        } catch (InterruptedException ex) {
            throw new RuntimeException("Thread was abruptly interrupted.", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("Exception thrown at running task.", ex);
        }
    }

    private int getNumWorkers(int size) {
        return Math.min(size, numThreads);
    }
}
