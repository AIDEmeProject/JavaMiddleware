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

package explore;

import data.IndexedDataset;
import data.preprocessing.StandardScaler;
import explore.user.FactoredUser;
import explore.user.User;
import explore.user.UserStub;
import io.FolderManager;
import io.TaskReader;
import machinelearning.active.learning.QueryByDisagreement;
import config.ExperimentConfiguration;


public final class Experiment {
    private final FolderManager experimentFolder;
    private Explore explore;
    private Evaluate evaluate;

    public Experiment(FolderManager experimentFolder) {
        this.experimentFolder = experimentFolder;
        this.explore = null;
        this.evaluate = null;
    }

    private void initialize() {
        ExperimentConfiguration configuration = experimentFolder.getExperimentConfig();

        TaskReader reader = new TaskReader(configuration.getTask());

        // if no initial sampler was set in the config, try to set the default one
        if (configuration.getInitialSampler() == null) {
            configuration.setInitialSampler(reader.getTaskConfig().getDefaultInitialSampler());
        }

        IndexedDataset rawData = reader.readData();
        IndexedDataset scaledData = rawData.copyWithSameIndexes(StandardScaler.fitAndTransform(rawData.getData()));
        User user = getUser(configuration, reader);

        if(configuration.hasFactorizationInformation()) {
            scaledData.setFactorizationStructure(configuration.getTsmConfiguration().getColumnPartitionIndexes());
        }

        if (configuration.getActiveLearner() instanceof QueryByDisagreement) {
            ((QueryByDisagreement) configuration.getActiveLearner()).setDataset(scaledData);
        }

        explore = new Explore(experimentFolder, configuration, scaledData, user);
        evaluate = new Evaluate(experimentFolder, configuration, scaledData, user);
    }

    private User getUser(ExperimentConfiguration configuration, TaskReader reader) {
        if (configuration.hasFactorizationInformation() || configuration.hasMultiTSM()) {
            return new FactoredUser(reader.readFactorizedTargetSetKeys(configuration.getTsmConfiguration()));
        } else {
            return new UserStub(reader.readTargetSetKeys());
        }
    }

    public Explore getExplore() {
        if (explore == null) {
            initialize();
        }
        return explore;
    }

    public Evaluate getEvaluate() {
        if (evaluate == null) {
            initialize();
        }
        return evaluate;
    }
}
