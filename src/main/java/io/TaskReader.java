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
         * True predicates in each subspace
         */
        private final String[] subpredicates;
        private boolean overlapping = false;

        /**
         * Default TSM flags for Multiple TSM algorithm
         */
        private final ArrayList<boolean[]> tsmFlags;

        private final InitialSampler defaultInitialSampler;

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

            this.subpredicates = config.containsKey("subpredicates") ? parseSubpredicates(config.get("subpredicates")) : defaultPredicatePartitioning();
            Validator.assertEquals(subpredicates.length, featureGroups.size());

            if (config.containsKey("posId")) {
                this.defaultInitialSampler = parseInitialSampler(config);
            }
            else {
                this.defaultInitialSampler = new StratifiedSampler(1, 1);
            }
        }

        private String[] defaultPredicatePartitioning() {
            if (overlapping) {
                throw new RuntimeException("Cannot have overlapping feature groups without specifying the subpredicates.");
            }

            Map<Integer, StringJoiner> predicateBuilder = new HashMap<>();

            for (String predicate : predicateSplittingPattern.split(predicate)) {
                int partitionNumber = getPartitionNumber(predicate);

                if (predicateBuilder.containsKey(partitionNumber)) {
                    predicateBuilder.get(partitionNumber).add(predicate);
                }
                else {
                    StringJoiner joiner = new StringJoiner(" AND ");
                    joiner.add(predicate);
                    predicateBuilder.put(partitionNumber, joiner);
                }
            }

            String[] factorizedPredicates = new String[featureGroups.size()];
            for (int i = 0; i < factorizedPredicates.length; i++) {
                StringJoiner joiner = predicateBuilder.get(i);

                if (joiner == null) {
                    throw new RuntimeException("There are no predicates containing attributes of feature group " + i);
                }

                factorizedPredicates[i] = joiner.toString();
            }

            return factorizedPredicates;
        }

        private int getPartitionNumber(String predicate) {
            int partitionNumber = -1, i = 0;
            for (String[] featureGroup : featureGroups) {
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

        private String[] parseSubpredicates(String config) {
            if (config.isEmpty()){
                return new String[]{predicate};
            }

            List<String> ans = new ArrayList<>();
            Matcher matcher = featureGroupMatcher.matcher(config);
            while(matcher.find()) {
                ans.add(matcher.group(1));
            }

            return ans.toArray(new String[0]);
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

                // check for extra columns
                if (!Arrays.asList(columns).containsAll(Arrays.asList(featureGroup))) {
                    throw new RuntimeException("Feature group " + Arrays.toString(featureGroup) + " contains a extra column.");
                }

                featureGroups.add(featureGroup);
            }

            // check if there are missing attributes in the feature groups
            HashSet<String> set = new HashSet<>();
            featureGroups.stream()
                    .map(Arrays::asList)
                    .forEach(set::addAll);

            if (set.size() < columns.length){
                throw new RuntimeException("Some attributes are missing from feature groups: " + Arrays.stream(columns).filter(x -> !set.contains(x)).collect(Collectors.toList()));
            }

            overlapping = set.size() != featureGroups.stream().map(x -> x.length).reduce(0, (x,y) -> x + y);

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
