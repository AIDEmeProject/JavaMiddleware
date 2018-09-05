package io;

import data.LabeledPoint;
import explore.ExperimentConfiguration;
import json.JsonConverter;

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
    private static String TEMP_EXT = ".tmp";

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
        return getPathToFile(index + RUN_EXT);
    }

    public Path getEvalFile(int index) {
        return getPathToFile(index + EVAL_EXT);
    }

    public Path getTempFile(int index) {
        return getPathToFile(index + TEMP_EXT);
    }

    private Path getPathToFile(String filename) {
        return folder.resolve(filename);
    }

    public ExperimentConfiguration parseConfigurationFile() {
        ExperimentConfiguration configuration;
        try {
            Path config = getPathToFile(CONFIG_FILE);
            Reader reader = new FileReader(config.toFile());
            configuration = JsonConverter.deserialize(reader, ExperimentConfiguration.class);
        }
        catch (FileNotFoundException ex) {
            throw new RuntimeException(CONFIG_FILE + " file not found on " + folder);
        }

        if (configuration == null) {
            throw new RuntimeException("Empty config file on folder " + folder);
        }

        return configuration;
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
}
