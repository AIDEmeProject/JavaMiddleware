package io;

import data.IndexedDataset;

import java.util.Map;
import java.util.Set;

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
        ConnectionConfiguration connectionConfig = new ConnectionConfiguration(datasetConfig.connection);
        reader = new DatabaseReader(connectionConfig.url, datasetConfig.database, connectionConfig.user, connectionConfig.password);
    }

    public IndexedDataset readData(){
        return reader.readTable(datasetConfig.table, datasetConfig.key, taskConfig.columns);
    }

    public Set<Long> readTargetSetKeys(){
        return reader.readKeys(datasetConfig.table, datasetConfig.key, taskConfig.predicate);
    }

    /**
     * This class holds the task configuration properties
     */
    private class TaskConfiguration {
        /**
         * Dataset name (in datasets.ini file)
         */
        private final String dataset;

        /**
         * Columns to read
         */
        private final String[] columns;

        /**
         * Predicate defining the target set (the WHERE clause in a SQL query)
         */
        private final String predicate;

        TaskConfiguration(String task) {
            IniConfigurationParser parser = new IniConfigurationParser("tasks");
            Map<String, String> config = parser.read(task);
            this.columns = config.get("columns").split(",");
            this.dataset = config.get("dataset");
            this.predicate = config.get("predicate");
        }
    }

    /**
     * This class holds the dataset's configuration properties
     */
    private class DatasetConfiguration {
        /**
         * Connection name (in connections.ini file)
         */
        private final String connection;

        /**
         * Database name
         */
        private final String database;

        /**
         * Table name
         */
        private final String table;

        /**
         * Column to use as key
         */
        private final String key;

        DatasetConfiguration(String dataset) {
            IniConfigurationParser parser = new IniConfigurationParser("datasets");
            Map<String, String> config = parser.read(dataset);
            this.connection = config.get("connection");
            this.database = config.get("table");
            this.table = config.get("table");
            this.key = config.get("key");
        }
    }

    /**
     * This class holds the database connection configuration properties
     */
    private class ConnectionConfiguration {
        /**
         * Database connection url (something on the format driver://url:port)
         */
        private final String url;

        /**
         * Database user
         */
        private final String user;

        /**
         * Database password
         */
        private final String password;

        ConnectionConfiguration(String connection) {
            IniConfigurationParser parser = new IniConfigurationParser("connections");
            Map<String, String> config = parser.read(connection);
            this.url = config.get("url");
            this.user = config.get("user");
            this.password = config.get("password");
        }
    }
}
