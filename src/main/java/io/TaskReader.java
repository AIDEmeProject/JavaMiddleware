package io;

import data.IndexedDataset;
import explore.ExperimentConfiguration;
import explore.sampling.FixedSampler;
import explore.sampling.InitialSampler;
import explore.sampling.StratifiedSampler;
import utils.Validator;

import java.util.*;
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

    private static final Pattern predicateSplittingPattern = Pattern.compile("\\s+AND\\s+", Pattern.CASE_INSENSITIVE);

    public TaskReader(String task) {
        taskConfig = new TaskConfiguration(task);
        datasetConfig = new DatasetConfiguration(taskConfig.dataset);
        ConnectionConfiguration connectionConfig = new ConnectionConfiguration(datasetConfig.connection);
        reader = new DatabaseReader(connectionConfig.url, datasetConfig.database, connectionConfig.user, connectionConfig.password);
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

    public List<Set<Long>> readFactorizedTargetSetKeys(ExperimentConfiguration.TsmConfiguration tsmConfiguration){
        // if no factorization structure is specified in run.py, set default ones
        tsmConfiguration.setColumns(taskConfig.columns);

        if(tsmConfiguration.getFeatureGroups().isEmpty()) {
            tsmConfiguration.setFeatureGroups(taskConfig.featureGroups);
        }

        if(tsmConfiguration.getFlags().isEmpty()) {
            tsmConfiguration.setFlags(taskConfig.tsmFlags);
        }

        System.out.println(tsmConfiguration);

        // match predicates to feature groups
        Map<Integer, StringJoiner> predicateBuilder = new HashMap<>();
        for (String predicate : predicateSplittingPattern.split(taskConfig.predicate)) {
            int partitionNumber = getPartitionNumber(tsmConfiguration, predicate);

            if (predicateBuilder.containsKey(partitionNumber)) {
                predicateBuilder.get(partitionNumber).add(predicate);
            }
            else {
                StringJoiner joiner = new StringJoiner(" AND ");
                joiner.add(predicate);
                predicateBuilder.put(partitionNumber, joiner);
            }
        }

        String[] factorizedPredicates = new String[tsmConfiguration.getFeatureGroups().size()];
        for (int i = 0; i < factorizedPredicates.length; i++) {
            StringJoiner joiner = predicateBuilder.get(i);

            if (joiner == null) {
                throw new RuntimeException("There are no predicates containing attributes of feature group " + i);
            }

            factorizedPredicates[i] = joiner.toString();
        }

        return Arrays.stream(factorizedPredicates)
                .map(subPredicate -> reader.readKeys(datasetConfig.table, datasetConfig.key, subPredicate))
                .collect(Collectors.toList());
    }

    private int getPartitionNumber(ExperimentConfiguration.TsmConfiguration tsmConfiguration, String predicate) {
        int partitionNumber = -1, i = 0;
        for (String[] featureGroup : tsmConfiguration.getFeatureGroups()) {
            if (Arrays.stream(featureGroup).anyMatch(predicate::contains)) {
                if (partitionNumber < 0) {
                    partitionNumber = i;
                }
                else {
                    throw new RuntimeException("Predicate \"" + predicate + "\" matches more than one partition: " + partitionNumber + " and " + i);
                }
            }
            i++;
        }
        if (partitionNumber < 0) {
            throw new RuntimeException("There is no feature group associated with predicate \"" + predicate + "\"");
        }

        return partitionNumber;
    }

    /**
     * This class holds the task configuration properties
     */
    public static class TaskConfiguration {
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
        private final ArrayList<String[]> featureGroups;

        /**
         * Default TSM flags for Multiple TSM algorithm
         */
        private final ArrayList<boolean[]> tsmFlags;

        private final InitialSampler defaultInitialSampler;

        private static final Pattern splitPattern = Pattern.compile("\\s*,\\s*");
        private static final Pattern featureGroupMatcher = Pattern.compile("\\[\\s*(.*?)\\s*\\]");

        TaskConfiguration(String task) {
            Map<String, String> config = new IniConfigurationParser("tasks").read(task);

            this.columns = splitPattern.split(config.get("columns"));
            Validator.assertNotEmpty(columns);

            this.dataset = Validator.assertNotEmpty(config.get("dataset"));
            this.predicate = Validator.assertNotEmpty(config.get("predicate"));

            this.featureGroups = parseFeatureGroups(config.getOrDefault("feature_groups", ""));
            this.tsmFlags = parseTsmFlags(config);
            Validator.assertEquals(featureGroups.size(), tsmFlags.size());

            if (config.containsKey("posId")) {
                this.defaultInitialSampler = parseInitialSampler(config);
            }
            else {
                this.defaultInitialSampler = new StratifiedSampler(1, 1);
            }
        }

        private InitialSampler parseInitialSampler(Map<String, String> config) {
            long posId = Long.parseLong(config.get("posId"));
            long[] negIds = splitPattern.splitAsStream(config.get("negIds")).mapToLong(Long::parseLong).toArray();

            return new FixedSampler(posId, negIds);
        }

        public InitialSampler getDefaultInitialSampler() {
            return defaultInitialSampler;
        }

        private ArrayList<String[]> parseFeatureGroups(String s) {
            ArrayList<String[]> featureGroups = new ArrayList<>();

            if (s.isEmpty()) {
                System.out.println("feature_groups not defined in config file, using single partition instead.");
                featureGroups.add(columns);
                return featureGroups;
            }
            Matcher matcher = featureGroupMatcher.matcher(s);
            while(matcher.find()) {
                String[] featureGroup = splitPattern.splitAsStream(matcher.group(1))
                        .map(String::trim)
                        .toArray(String[]::new);

                // if matched a empty bracket []
                if (featureGroup.length == 0) {
                    throw new RuntimeException("Found empty feature group: " + matcher.group(1));
                }

                if (!Arrays.asList(columns).containsAll(Arrays.asList(featureGroup))) {
                    throw new RuntimeException("Feature group " + Arrays.toString(featureGroup) + " contains a extra column.");
                }

                featureGroups.add(featureGroup);
            }

            if (featureGroups.stream().map(x -> x.length).reduce(0, (x,y) -> x+y) != columns.length) {
                throw new RuntimeException("Feature groups contains repeated or missing columns.");
            }

            return featureGroups;
        }

        private ArrayList<boolean[]> parseTsmFlags(Map<String, String> config) {
            List<Boolean> isPositiveRegionConvex = parseBooleanList(config.getOrDefault("is_convex_positive", ""));
            List<Boolean> isCategorical = parseBooleanList(config.getOrDefault("is_categorical", ""));

            int size = isPositiveRegionConvex.size();

            if (isCategorical.size() != size) {
                throw new RuntimeException("is_convex_positive and is_categorical configs have different sizes!");
            }

            if (size == 0) {
                System.out.println("TSM flags not defined in config file, using default values: [true, false] for all feature groups.");
                size = featureGroups.size();
                for (int i = 0; i < size; i++) {
                    isPositiveRegionConvex.add(true);
                    isCategorical.add(false);
                }
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
