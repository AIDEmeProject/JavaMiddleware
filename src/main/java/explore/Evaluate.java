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
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.metrics.MetricCalculator;
import explore.user.User;
import io.FolderManager;
import io.json.JsonConverter;
import utils.RandomState;
import config.ExperimentConfiguration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public final class Evaluate {
    private final FolderManager folder;
    private final ExperimentConfiguration configuration;
    private final User user;
    private final IndexedDataset dataPoints;

    public Evaluate(FolderManager folder, ExperimentConfiguration configuration, IndexedDataset dataPoints, User user) {
        this.folder = folder;
        this.configuration = configuration;
        this.dataPoints = dataPoints;
        this.user = user;
    }

    public void evaluate(int id, String calculatorIdentifier) {
        Path evalFile = folder.getEvalFile(calculatorIdentifier, id);

        MetricCalculator metricCalculator = folder.getMetricCalculator(calculatorIdentifier);

        PartitionedDataset partitionedDataset = configuration
                .getTsmConfiguration()
                .getMultiTsmModel()
                .map(x -> new PartitionedDataset(dataPoints, x))
                .orElseGet(() -> new PartitionedDataset(dataPoints));

        setRandomSeed(id);

        try (BufferedWriter evalFileWriter = Files.newBufferedWriter(evalFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            long iter = 0;
            long numberOfPreviousIters = countLinesOfFile(evalFile);

            for (List<LabeledPoint> labeledPoints : folder.getLabeledPoints(id)) {
                long start = System.nanoTime();
                partitionedDataset.update(labeledPoints);

                if (iter++ < numberOfPreviousIters) {
                    continue;
                }

                Map<String, Double> metrics = metricCalculator.compute(partitionedDataset, user).getMetrics();

                writeLineToFile(evalFileWriter, JsonConverter.serialize(metrics));
                System.out.println("#" + iter + ": " + (System.nanoTime() - start) / 1e9 + " seconds");
                System.out.println();
            }
        } catch (IOException ex) {
            throw new RuntimeException("evaluation failed.", ex);
        }
    }

    private void setRandomSeed(int id) {
        RandomState.setSeed(1000L * id);
    }

    private static long countLinesOfFile(Path file) {
        try {
            return Files.lines(file).filter(x -> !x.trim().isEmpty()).count();
        } catch (IOException ex) {
            throw new RuntimeException("IO error while counting lines of file " + file);
        }
    }

    private static void writeLineToFile(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }
}