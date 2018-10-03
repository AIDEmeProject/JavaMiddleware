package explore;

import data.DataPoint;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.statistics.Statistics;
import explore.statistics.StatisticsCollection;
import explore.user.BudgetedUser;
import explore.user.User;
import io.FolderManager;
import io.json.JsonConverter;
import machinelearning.active.Ranker;

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
    private final List<DataPoint> dataPoints;
    private final User user;
    private final ExperimentConfiguration configuration;

    /**
     * @param folder: the exploration folder where results will be stored
     * @param dataPoints: unlabeled pool of points
     * @param user: the user for labeling points
     */
    public Explore(FolderManager folder, List<DataPoint> dataPoints, User user) {
        this.folder = folder;
        this.dataPoints = dataPoints;
        this.user = user;
        this.configuration = folder.getExperimentConfig();
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

    private static Map<String, Double> computeTotalTimeMeasurements(StatisticsCollection metrics) {
        Map<String, Double> sum = new HashMap<>();
        for (Statistics statistics : metrics) {
            sum.put(statistics.getName(), statistics.getSum());
        }
        return sum;
    }

    private PartitionedDataset getPartitionedDataset(int id) {
        PartitionedDataset partitionedDataset = new PartitionedDataset(dataPoints);  // TODO: add TSM here
        folder.getLabeledPoints(id).forEach(partitionedDataset::update);
        return partitionedDataset;
    }

    private static void writeLineToFile(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }
}