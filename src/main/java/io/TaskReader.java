package io;

import data.DataPoint;
import utils.Validator;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        ConnectionConfiguration connectionConfig = new ConnectionConfiguration(datasetConfig.connection);
        reader = new DatabaseReader(connectionConfig.url, datasetConfig.database, connectionConfig.user, connectionConfig.password);
    }

    public List<DataPoint> readData(){
        return reader.readTable(datasetConfig.table, datasetConfig.key, taskConfig.columns);
    }

    public Set<Long> readTargetSetKeys(){
        return reader.readKeys(datasetConfig.table, datasetConfig.key, taskConfig.predicate);
    }

    /**
     * This class holds the task configuration properties
     */
    private static class TaskConfiguration {
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

        /**
         * Default feature groups for Multiple TSM
         */
        private final ArrayList<int[]> featureGroups;

        /**
         * Default TSM flags for Multiple TSM algorithm
         */
        private final ArrayList<boolean[]> tsmFlags;

        private static final Pattern splitPattern = Pattern.compile("\\s*,\\s*");
        private static final Pattern featureGroupMatcher = Pattern.compile("\\[\\s*(.*?)\\s*\\]");
        private static final Pattern predicateSplittingPattern = Pattern.compile("\\s+AND\\s+", Pattern.CASE_INSENSITIVE);

        TaskConfiguration(String task) {
            Map<String, String> config = new IniConfigurationParser("tasks").read(task);

            this.columns = splitPattern.split(config.get("columns"));
            Validator.assertNotEmpty(columns);

            this.dataset = Validator.assertNotEmpty(config.get("dataset"));
            this.predicate = Validator.assertNotEmpty(config.get("predicate"));

            this.featureGroups = parseFeatureGroups(config.getOrDefault("feature_groups", ""));
            this.tsmFlags = parseTsmFlags(config);
            Validator.assertEquals(featureGroups.size(), tsmFlags.size());
        }

        private ArrayList<int[]> parseFeatureGroups(String s) {
            ArrayList<int[]> featureGroups = new ArrayList<>();

            Matcher matcher = featureGroupMatcher.matcher(s);
            while(matcher.find()) {
                int[] indexOfAttributes = splitPattern.splitAsStream(matcher.group(1))
                        .mapToInt(this::findIndexOfColumn)
                        .toArray();

                // if matched a empty bracket []
                if (indexOfAttributes.length == 0) {
                    throw new RuntimeException("Found empty feature group: " + matcher.group(1));
                }

                featureGroups.add(indexOfAttributes);
            }

            return featureGroups;
        }

        private int findIndexOfColumn(String column) {
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equals(column)) {
                    return i;
                }
            }
            throw new RuntimeException("Error while parsing feature groups: column \"" + column + "\" not in list of columns.");
        }

        private ArrayList<boolean[]> parseTsmFlags(Map<String, String> config) {
            List<Boolean> isPositiveRegionConvex = parseBooleanList(config.getOrDefault("is_convex_positive", ""));
            List<Boolean> isCategorical = parseBooleanList(config.getOrDefault("is_categorical", ""));

            int size = isPositiveRegionConvex.size();
            if (isCategorical.size() != size) {
                throw new RuntimeException("is_convex_positive and is_categorical configs have different sizes!");
            }

            ArrayList<boolean[]> tsmFlags = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                tsmFlags.add(new boolean[]{isPositiveRegionConvex.get(i), isCategorical.get(i)});
            }
            return tsmFlags;
        }

        private List<Boolean> parseBooleanList(String s) {
            return splitPattern.splitAsStream(s)
                    .map(Boolean::parseBoolean)
                    .collect(Collectors.toList());
        }
    }

    /**
     * This class holds the dataset's configuration properties
     */
    private static class DatasetConfiguration {
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
            this.database = config.get("database");
            this.table = config.get("table");
            this.key = config.get("key");
        }
    }

    /**
     * This class holds the database connection configuration properties
     */
    private static class ConnectionConfiguration {
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
