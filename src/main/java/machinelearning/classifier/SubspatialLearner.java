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

import data.LabeledDataset;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


/**
 * Trains a learner object per subspace, returning a {@link SubspatialClassifier}. Training for each subspace is done
 * concurrently.
 */
public class SubspatialLearner implements Learner {
    /**
     * Learners to train on each subspace
     */
    private final Learner[] subspaceLearners;

    /**
     * Multi-threaded runner
     */
    private final SubspatialWorker worker;

    public SubspatialLearner(Learner[] subspaceLearners, SubspatialWorker worker) {
        Validator.assertNotEmpty(subspaceLearners);

        this.subspaceLearners = subspaceLearners;
        this.worker = Objects.requireNonNull(worker);
    }

    @Override
    public SubspatialClassifier fit(LabeledDataset labeledPoints) {
        Validator.assertEquals(labeledPoints.partitionSize(), subspaceLearners.length);

        LabeledDataset[] partitionedData = labeledPoints.getPartitionedData();
        int size = partitionedData.length;

        // create list of tasks to be run
        List<Callable<Classifier>> workers = new ArrayList<>(size);

        for(int i = 0; i < size; i++){
            workers.add(new LearnerWorker(partitionedData[i], subspaceLearners[i]));
        }

        return new SubspatialClassifier(
                labeledPoints.getPartitionIndexes(),
                worker.run(workers).toArray(new Classifier[0]),
                worker
        );
    }

    /**
     * Helper class for multi-threaded fit() method
     */
    private static class LearnerWorker implements Callable<Classifier> {

        private final LabeledDataset labeledData;
        private final Learner learner;

        LearnerWorker(LabeledDataset labeledData, Learner learner) {
            this.labeledData = labeledData;
            this.learner = learner;
        }

        @Override
        public Classifier call() {
            return learner.fit(labeledData);
        }
    }
}
