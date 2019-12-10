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
