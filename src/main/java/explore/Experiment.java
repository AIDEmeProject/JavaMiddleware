package explore;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import explore.sampling.InitialSampler;
import explore.sampling.ReservoirSampler;
import explore.sampling.StratifiedSampler;
import explore.user.User;
import json.JsonConverter;
import machinelearning.active.ActiveLearner;
import utils.Validator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Experiment {
    private LabeledDataset labeledDataset;
    private User user;
    private ActiveLearner activeLearner;

    private InitialSampler initialSampler;
    private int subsampleSize;
    private int budget;

    public static class Builder {
        private LabeledDataset labeledDataset;
        private User user;
        private ActiveLearner activeLearner;

        private int subsampleSize = Integer.MAX_VALUE;
        private int budget = 100;
        private InitialSampler initialSampler = new StratifiedSampler(1, 1);

        public Builder(LabeledDataset labeledDataset, User user, ActiveLearner activeLearner) {
            this.labeledDataset = Objects.requireNonNull(labeledDataset);
            this.user = Objects.requireNonNull(user);
            this.activeLearner = Objects.requireNonNull(activeLearner);
        }

        public Builder subsample(int subsampleSize) {
            Validator.assertPositive(subsampleSize);
            this.subsampleSize = subsampleSize;
            return this;
        }

        public Builder budget(int budget) {
            Validator.assertPositive(budget);
            this.budget = budget;
            return this;
        }

        public Builder initialSampler(InitialSampler initialSampler) {
            this.initialSampler = Objects.requireNonNull(initialSampler);
            return this;
        }

        public Experiment build() {
            resetAndUpdateActiveLearner();
            return new Experiment(this);
        }

        private void resetAndUpdateActiveLearner() {
            this.activeLearner.clear();
            if (labeledDataset.hasLabeledPoints()) {
                this.activeLearner.update(labeledDataset.getLabeledPoints());
            }
        }
    }

    private Experiment(Builder builder) {
        this.labeledDataset = new LabeledDataset(builder.labeledDataset);
        this.user = builder.user;
        this.activeLearner = builder.activeLearner;
        this.subsampleSize = builder.subsampleSize;
        this.budget = builder.budget;
        this.initialSampler = builder.initialSampler;
    }

    public void run(BufferedWriter labeledPointsWriter, BufferedWriter metricsWriter) throws IOException {
        for (int iter = 0; iter < budget && labeledDataset.hasUnlabeledPoints(); iter++) {
            runSingleIteration(labeledPointsWriter, metricsWriter);
        }
    }

    private void runSingleIteration(BufferedWriter labeledPointsWriter, BufferedWriter metricsWriter) throws IOException {
        Map<String, Double> metrics = new HashMap<>();
        long initialTime, start = System.nanoTime();

        // find next points to label
        initialTime = System.nanoTime();
        List<DataPoint> mostInformativePoint = getNextPointsToLabel();
        metrics.put("GetNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // ask user for labels
        initialTime = System.nanoTime();
        List<LabeledPoint> labeledPoints = user.getLabeledPoint(mostInformativePoint);
        metrics.put("UserTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled / unlabeled sets
        labeledDataset.putOnLabeledSet(labeledPoints);

        // update model
        initialTime = System.nanoTime();
        activeLearner.update(labeledPoints);
        metrics.put("FitTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // iter time
        metrics.put("IterTimeMillis",(System.nanoTime() - start) / 1e6);

        System.out.println(metrics);

        // save metrics to file
        writeLineToFile(labeledPointsWriter,  JsonConverter.serialize(labeledPoints));
        writeLineToFile(metricsWriter, JsonConverter.serialize(metrics));
    }

    private List<DataPoint> getNextPointsToLabel() {
        if (!labeledDataset.hasLabeledPoints()) {
            return initialSampler.runInitialSample(labeledDataset.getUnlabeledPoints(), user);
        }

        Collection<DataPoint> sample = ReservoirSampler.sample(labeledDataset.getUnlabeledPoints(), subsampleSize);
        return Collections.singletonList(activeLearner.getRanker().top(sample));
    }

    private static void writeLineToFile(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }

}
