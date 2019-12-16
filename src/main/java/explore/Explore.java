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

import config.ExperimentConfiguration;
import data.IndexedDataset;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.sampling.FixedSampler;
import explore.statistics.Statistics;
import explore.statistics.StatisticsCollection;
import explore.user.BudgetedUser;
import explore.user.User;
import io.FolderManager;
import io.json.JsonConverter;
import machinelearning.active.Ranker;
import utils.RandomState;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class Explore {
    private final FolderManager folder;
    private final IndexedDataset dataPoints;
    private final User user;
    private final ExperimentConfiguration configuration;

    /**
     * @param folder: the exploration folder where results will be stored
     * @param dataPoints: unlabeled pool of points
     * @param user: the user for labeling points
     */
    public Explore(FolderManager folder, ExperimentConfiguration configuration, IndexedDataset dataPoints, User user) {
        this.folder = folder;
        this.configuration = configuration;
        this.dataPoints = dataPoints;
        this.user = user;
    }

    /**
     * Start a new exploration process
     * @param id: run file id to create
     * @param budget: budget on the number of points labeled by the user
     */
    public void run(int id, int budget) {
        resume(id, budget, StandardOpenOption.CREATE_NEW);
    }

    /**
     * Resume a previous exploration process
     * @param id: run file id to resume
     * @param budget: budget on the number of new points labeled by the user
     */
    public void resume(int id, int budget) {
        resume(id, budget, StandardOpenOption.APPEND);
    }

    private void resume(int id, int budget, StandardOpenOption openOption) {
        setRandomSeed(id);

        // set run id for fixed sampler
        if (configuration.getInitialSampler() instanceof FixedSampler) {
            ((FixedSampler) configuration.getInitialSampler()).setId(id - 1);
        }

        PartitionedDataset partitionedDataset = getPartitionedDataset(id);
        BudgetedUser budgetedUser = new BudgetedUser(user, budget);

        try (BufferedWriter labeledPointsWriter = Files.newBufferedWriter(folder.getRunFile(id), openOption);
             BufferedWriter metricsWriter = Files.newBufferedWriter(folder.getEvalFile("Timing", id), openOption)) {

            Ranker ranker = null;
            if (partitionedDataset.hasLabeledPoints()) {
                ranker = configuration.getActiveLearner().fit(partitionedDataset.getLabeledPoints());
            }
            else {
                Iteration.Result result = new InitialIteration(configuration).run(partitionedDataset, user, ranker);
                ranker = result.getRanker();
                writeLineToFile(labeledPointsWriter, JsonConverter.serialize(result.getLabeledPoints()));
                writeLineToFile(metricsWriter, JsonConverter.serialize(result.getTimeMeasurements()));
            }

            Iteration iteration = new ExploreIteration(configuration);
            while (budgetedUser.isWilling() && partitionedDataset.hasUnknownPoints()) {
                List<LabeledPoint> labeledPoints = new ArrayList<>();
                StatisticsCollection timeMeasurements = new StatisticsCollection();

                int num = budgetedUser.getNumberOfLabeledPoints();

                while(budgetedUser.getNumberOfLabeledPoints() == num && partitionedDataset.hasUnknownPoints()) {
                    Iteration.Result result = iteration.run(partitionedDataset, budgetedUser, ranker);
                    ranker = result.getRanker();
                    labeledPoints.addAll(result.getLabeledPoints());
                    timeMeasurements.update(result.getTimeMeasurements());
                }

                writeLineToFile(labeledPointsWriter, JsonConverter.serialize(labeledPoints));
                writeLineToFile(metricsWriter, JsonConverter.serialize(computeTotalTimeMeasurements(timeMeasurements)));
            }

        } catch (Exception ex) {
            //TODO: log error
            throw new RuntimeException("Exploration failed.", ex);
        }
    }


    private void setRandomSeed(int id) {
        RandomState.setSeed(1000L * id);
    }


    private static Map<String, Double> computeTotalTimeMeasurements(StatisticsCollection metrics) {
        Map<String, Double> sum = new HashMap<>();
        for (Statistics statistics : metrics) {
            sum.put(statistics.getName(), statistics.getSum());
        }
        return sum;

    }

    private PartitionedDataset getPartitionedDataset(int id) {
        PartitionedDataset partitionedDataset = configuration
                .getTsmConfiguration()
                .getMultiTsmModel()
                .map(x -> new PartitionedDataset(dataPoints, x))
                .orElseGet(() -> new PartitionedDataset(dataPoints));

        folder.getLabeledPoints(id).forEach(partitionedDataset::update);
        return partitionedDataset;
    }

    private static void writeLineToFile(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }
}