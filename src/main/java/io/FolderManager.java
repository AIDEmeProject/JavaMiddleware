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

import data.LabeledPoint;
import config.ExperimentConfiguration;
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
    private static ExperimentConfiguration experimentConfiguration = null;
    private static String CONFIG_FILE = "config.json";
    private static String RUN_FILE = "Runs/%d.run";
    private static String EVAL_FILE = "%s/%d.eval";

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

    /**
     * @return the index of a new run file
     */
    public int getNewRunFileIndex() {
        File[] files = folder.resolve("Runs").toFile().listFiles(x -> x.getName().endsWith(".run"));

        if (files == null || files.length == 0) {
            return 1;
        }

        int index = Arrays.stream(files)
                .map(File::getName)
                .map(name -> name.substring(0, name.length() - 4))
                .map(Integer::parseInt)
                .max(Integer::compareTo)
                .get();

        return index + 1;
    }

    public Path getRunFile(int index) {
        return getFullPath(String.format(RUN_FILE, index));
    }

    public Path getEvalFile(String metric, int index) {
        try {
            if (Files.notExists(folder.resolve(metric))) {
                Files.createDirectories(folder.resolve(metric));
            }
        } catch (IOException ex) {
            throw new RuntimeException("IO error while creating directory.", ex);
        }

        return getFullPath(String.format(EVAL_FILE, metric, index));
    }

    public File[] getAllEvalFiles(String metric) {
        return folder.resolve(metric).toFile().listFiles(x -> x.getName().endsWith(".eval"));
    }

    public File getAverageFile(String metric) {
        return folder.resolve(metric).resolve("average.eval").toFile();
    }

    private Path getFullPath(String filename) {
        return folder.resolve(filename);
    }

    public List<List<LabeledPoint>> getLabeledPoints(int index) {
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
        if (experimentConfiguration == null) {
            experimentConfiguration = parseConfigFile(folder.resolve("Runs/" + CONFIG_FILE), ExperimentConfiguration.class);
        }
        return experimentConfiguration;
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
            throw new RuntimeException(CONFIG_FILE + " file not found on " + folder, ex);
        }

        if (parsedObject == null) {
            throw new RuntimeException("Empty config file on folder " + folder);
        }

        return parsedObject;
    }

}
