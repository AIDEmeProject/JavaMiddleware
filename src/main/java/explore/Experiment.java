package explore;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import data.preprocessing.StandardScaler;
import explore.metrics.MetricCalculator;
import explore.sampling.InitialSampler;
import explore.sampling.ReservoirSampler;
import explore.user.DummyUser;
import explore.user.User;
import io.FolderManager;
import io.TaskReader;
import io.json.JsonConverter;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.classifier.Label;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Experiment {
    private FolderManager folder;
    private List<DataPoint> dataPoints;
    private User user;
    private ActiveLearner activeLearner;
    private InitialSampler initialSampler;
    private int subsampleSize;

    public Experiment(FolderManager folder) {
        this.folder = folder;

        ExperimentConfiguration configuration = folder.getExperimentConfig();

        String task = configuration.getTask();
        TaskReader reader = new TaskReader(task);

        this.dataPoints = StandardScaler.fitAndTransform(reader.readData());

        Set<Long> positiveKeys = reader.readTargetSetKeys();
        this.user = new DummyUser(positiveKeys);

        this.activeLearner = configuration.getActiveLearner();
        this.initialSampler = configuration.getInitialSampler();
        this.subsampleSize = configuration.getSubsampleSize();
    }

    public int run(int budget) {
        int id = folder.getNewRunFileIndex();
        resume(id, budget, StandardOpenOption.CREATE_NEW);
        return id;
    }

    public void resume(int id, int budget) {
        resume(id, budget, StandardOpenOption.APPEND);
    }

    private void resume(int id, int budget, StandardOpenOption openOption) {
        LabeledDataset labeledDataset = new LabeledDataset(dataPoints);

        for (List<LabeledPoint> labeledPoints : folder.parseRunFile(id)) {
            labeledDataset.putOnLabeledSet(labeledPoints);
        }

        Ranker ranker = null;
        if (labeledDataset.hasLabeledPoints()) {
            ranker = activeLearner.fit(labeledDataset.getLabeledPoints());
        }

        try (BufferedWriter labeledPointsWriter = Files.newBufferedWriter(folder.getRunFile(id), openOption);
             BufferedWriter metricsWriter = Files.newBufferedWriter(folder.getEvalFile("Timing", id), openOption)) {

            while(labeledDataset.getNumLabeledPoints() <= budget && labeledDataset.hasUnlabeledPoints()) {
                ranker = runSingleIteration(labeledDataset, ranker, labeledPointsWriter, metricsWriter);
            }

        } catch (Exception ex) {
            //TODO: log error
            throw new RuntimeException("Exploration failed.", ex);
        }
    }

    private Ranker runSingleIteration(LabeledDataset labeledDataset, Ranker ranker, BufferedWriter labeledPointsWriter, BufferedWriter metricsWriter) throws IOException {
        Map<String, Double> metrics = new HashMap<>();
        long initialTime, start = System.nanoTime();

        // find next points to label
        initialTime = System.nanoTime();
        List<DataPoint> mostInformativePoint = getNextPointsToLabel(labeledDataset, ranker);
        metrics.put("GetNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // ask user for labels
        initialTime = System.nanoTime();
        List<LabeledPoint> labeledPoints = user.getLabeledPoint(mostInformativePoint);
        metrics.put("UserTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled / unlabeled sets
        labeledDataset.putOnLabeledSet(labeledPoints);

        // update model
        initialTime = System.nanoTime();
        ranker = activeLearner.fit(labeledDataset.getLabeledPoints());
        metrics.put("FitTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // iter time
        metrics.put("IterTimeMillis", (System.nanoTime() - start) / 1e6);

        // save metrics to file
        writeLineToFile(labeledPointsWriter,  JsonConverter.serialize(labeledPoints));
        writeLineToFile(metricsWriter, JsonConverter.serialize(metrics));

        return ranker;
    }

    private List<DataPoint> getNextPointsToLabel(LabeledDataset labeledDataset, Ranker ranker) {
        if (!labeledDataset.hasLabeledPoints()) {
            return initialSampler.runInitialSample(labeledDataset.getUnlabeledPoints(), user);
        }

        Collection<DataPoint> sample = ReservoirSampler.sample(labeledDataset.getUnlabeledPoints(), subsampleSize);
        return Collections.singletonList(ranker.top(sample));
    }

    public void evaluate(int id, String calculatorIdentifier) {
        MetricCalculator metricCalculator = folder.getMetricCalculator(calculatorIdentifier);

        Label[] trueLabels = user.getLabel(dataPoints);
        LabeledDataset labeledDataset = new LabeledDataset(dataPoints);

        Path evalFile = folder.getEvalFile(calculatorIdentifier, id);

        try (BufferedWriter evalFileWriter = Files.newBufferedWriter(evalFile)) {

            for (List<LabeledPoint> labeledPoints : folder.parseRunFile(id)) {
                labeledDataset.putOnLabeledSet(labeledPoints);

                Map<String, Double> metrics = metricCalculator.compute(labeledDataset, trueLabels).getMetrics();

                writeLineToFile(evalFileWriter, JsonConverter.serialize(metrics));
            }
        } catch (IOException ex) {
            throw new RuntimeException("evaluation failed.", ex);
        }
    }

    private static void writeLineToFile(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }
}
