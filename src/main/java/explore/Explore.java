package explore;

import data.DataPoint;
import data.ExtendedLabel;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.sampling.InitialSampler;
import explore.sampling.ReservoirSampler;
import explore.user.BudgetedUser;
import explore.user.User;
import io.FolderManager;
import io.json.JsonConverter;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.classifier.Label;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public final class Explore {
    private FolderManager folder;
    private List<DataPoint> dataPoints;
    private User user;
    private ActiveLearner activeLearner;
    private InitialSampler initialSampler;
    private int subsampleSize;
    private double searchUncertainRegionProbability;

    public Explore(FolderManager folder, List<DataPoint> dataPoints, User user) {
        this.folder = folder;
        this.dataPoints = dataPoints;
        this.user = user;

        ExperimentConfiguration configuration = folder.getExperimentConfig();
        this.activeLearner = configuration.getActiveLearner();
        this.initialSampler = configuration.getInitialSampler();
        this.subsampleSize = configuration.getSubsampleSize();
        this.searchUncertainRegionProbability = configuration.getSearchUncertainRegionProbability();
    }

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
        PartitionedDataset partitionedDataset = new PartitionedDataset(dataPoints);
        BudgetedUser budgetedUser = new BudgetedUser(user, budget);

        for (List<LabeledPoint> labeledPoints : folder.getLabeledPoints(id)) {
            partitionedDataset.update(labeledPoints);
        }

        Ranker ranker = null;  // TODO: avoid setting ranker to null (move initial sampling here?)
        if (partitionedDataset.hasLabeledPoints()) {
            ranker = activeLearner.fit(partitionedDataset.getLabeledPoints());
        }

        try (BufferedWriter labeledPointsWriter = Files.newBufferedWriter(folder.getRunFile(id), openOption);
             BufferedWriter metricsWriter = Files.newBufferedWriter(folder.getEvalFile("Timing", id), openOption)) {

            while (budgetedUser.isWilling() && partitionedDataset.hasUnknownPoints()) {
                ranker = runSingleIteration(partitionedDataset, budgetedUser, ranker, labeledPointsWriter, metricsWriter);
            }

        } catch (Exception ex) {
            //TODO: log error
            throw new RuntimeException("Exploration failed.", ex);
        }
    }

    private Ranker runSingleIteration(PartitionedDataset partitionedDataset, BudgetedUser budgetedUser, Ranker ranker, BufferedWriter labeledPointsWriter, BufferedWriter metricsWriter) throws IOException {
        Map<String, Double> metrics = new HashMap<>();
        long initialTime, start = System.nanoTime();

        // find next points to label
        initialTime = System.nanoTime();
        List<DataPoint> mostInformativePoints = getNextPointsToLabel(partitionedDataset, ranker);
        metrics.put("GetNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // ask user for labels
        initialTime = System.nanoTime();
        List<LabeledPoint> labeledPoints = getDataPointsLabels(partitionedDataset, budgetedUser, mostInformativePoints);
        metrics.put("UserTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled / unlabeled partitions
        initialTime = System.nanoTime();
        partitionedDataset.update(labeledPoints);
        metrics.put("UpdatePartitionsTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update model
        initialTime = System.nanoTime();
        ranker = activeLearner.fit(partitionedDataset.getLabeledPoints());
        metrics.put("FitTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // iter time
        metrics.put("IterTimeMillis", (System.nanoTime() - start) / 1e6);

        // save metrics to file
        // TODO: we should accumulate labeled points until the user labels at least one point
        writeLineToFile(labeledPointsWriter, JsonConverter.serialize(labeledPoints));
        writeLineToFile(metricsWriter, JsonConverter.serialize(metrics));

        return ranker;
    }

    private List<LabeledPoint> getDataPointsLabels(PartitionedDataset partitionedDataset, BudgetedUser budgetedUser, List<DataPoint> mostInformativePoints) {
        List<LabeledPoint> labeledPoints = new ArrayList<>();
        for (DataPoint dataPoint : mostInformativePoints) {
            ExtendedLabel extendedLabel = partitionedDataset.getLabel(dataPoint);
            Label label = extendedLabel == ExtendedLabel.UNKNOWN ? budgetedUser.getLabel(dataPoint) : extendedLabel.toLabel();
            labeledPoints.add(new LabeledPoint(dataPoint, label));
        }
        return labeledPoints;
    }

    private List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, Ranker ranker) {
        if (!partitionedDataset.hasLabeledPoints()) {
            return initialSampler.runInitialSample(partitionedDataset.getUnlabeledPoints(), user);  // do not use BudgetedUser since the initial sampling does not count against the budget
        }

        List<DataPoint> unlabeledData = new Random().nextDouble() <= searchUncertainRegionProbability ? partitionedDataset.getUncertainPoints() : partitionedDataset.getUnlabeledPoints();
        Collection<DataPoint> sample = ReservoirSampler.sample(unlabeledData, subsampleSize);
        return Collections.singletonList(ranker.top(sample));
    }

    private static void writeLineToFile(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }
}