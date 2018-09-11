package io;

import data.LabeledPoint;
import explore.ExperimentConfiguration;
import explore.metrics.MetricCalculator;
import io.json.JsonConverter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class manages a folder in the local filesystem. It can create new files in the folder, and return a list of
 */
public class FolderManager {
    private static String CONFIG_FILE = "config.json";
    private static String RUN_EXT = ".run";
    private static String EVAL_EXT = ".eval";

    /**
     * File object pointing to folder
     */
    private final Path folder;

    /**
     * @param path: path to folder
     * @throws RuntimeException if folder fails to be created or if path is not a directory
     */
    public FolderManager(String path) {
        folder = Paths.get(path);

        if (!Files.isDirectory(folder)){
            throw new RuntimeException("Path " + path + " does not represent an existing directory.");
        }
    }

    public int getNewRunFileIndex() {
        File[] files = folder.toFile().listFiles(x -> x.getName().endsWith(".run"));

        if (files == null || files.length == 0) {
            return 1;
        }

        Arrays.sort(files);
        String name = files[files.length - 1].getName();
        int index = Integer.parseInt(name.substring(0, name.length() - 4));
        return index + 1;
    }

    public Path getRunFile(int index) {
        return getPathToFile("Runs", index + RUN_EXT);
    }

    public Path getEvalFile(String metric, int index) {
        return getPathToFile(metric, index + EVAL_EXT);
    }

    private Path getPathToFile(String metric, String filename) {
        return folder.resolve(metric).resolve(filename);
    }

    public List<List<LabeledPoint>> parseRunFile(int index) {
        if (Files.notExists(getRunFile(index))) {
            return Collections.emptyList();
        }

        try(BufferedReader reader = Files.newBufferedReader(getRunFile(index))) {
            return reader.lines()
                    .map(JsonConverter::deserializeLabeledPoints)
                    .collect(Collectors.toList());
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to parse run file.", ex);
        }
    }

    public ExperimentConfiguration getExperimentConfig() {
        return parseConfigFile(folder.resolve(CONFIG_FILE), ExperimentConfiguration.class);
    }

    public MetricCalculator getMetricCalculator(String name) {
        Path path = folder.resolve(name);

        if (!Files.isDirectory(path)) {
            throw new RuntimeException("Folder " + path + " does not exist.");
        }

        return parseConfigFile(path.resolve(CONFIG_FILE), MetricCalculator.class);
    }

    private <T> T parseConfigFile(Path config, Class<T> type) {
        T parsedObject = null;
        try {
            Reader reader = new FileReader(config.toFile());
            parsedObject = JsonConverter.deserialize(reader, type);
        }
        catch (FileNotFoundException ex) {
            throw new RuntimeException(CONFIG_FILE + " file not found on " + folder);
        }

        if (parsedObject == null) {
            throw new RuntimeException("Empty config file on folder " + folder);
        }

        return parsedObject;
    }

}
