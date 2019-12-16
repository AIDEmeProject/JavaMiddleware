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

package io;

import config.DatasetConfiguration;
import config.TaskConfiguration;
import config.TsmConfiguration;
import data.IndexedDataset;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This module is responsible for reading a task configuration file in resources/tasks.ini file, and any necessary
 * complementary configuration information (dataset, connection, ...). From the configuration file, it can read the
 * data table from a given data source and the positive set keys.
 */
public class TaskReader {
    private final TaskConfiguration taskConfig;
    private final DatasetConfiguration datasetConfig;
    private final DatabaseReader reader;

    public TaskReader(String task) {
        taskConfig = new TaskConfiguration(task);
        datasetConfig = new DatasetConfiguration(taskConfig.dataset);
        reader = datasetConfig.buildReader();
    }

    public TaskConfiguration getTaskConfig() {
        return taskConfig;
    }

    public IndexedDataset readData(){
        return reader.readTable(datasetConfig.table, datasetConfig.key, taskConfig.columns);
    }

    public Set<Long> readTargetSetKeys(){
        return reader.readKeys(datasetConfig.table, datasetConfig.key, taskConfig.predicate);
    }

    public List<Set<Long>> readFactorizedTargetSetKeys(TsmConfiguration tsmConfiguration){
        // set factorization structure specified in tasks.ini
        tsmConfiguration.setColumns(taskConfig.columns);
        tsmConfiguration.setFeatureGroups(taskConfig.featureGroups);
        tsmConfiguration.setFlags(taskConfig.tsmFlags);

        System.out.println(tsmConfiguration);

        String[] factorizedPredicates = taskConfig.subpredicates.clone();

        System.out.println(Arrays.toString(factorizedPredicates));

        return Arrays.stream(factorizedPredicates)
                .map(subPredicate -> reader.readKeys(datasetConfig.table, datasetConfig.key, subPredicate))
                .collect(Collectors.toList());
    }
}
