/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
