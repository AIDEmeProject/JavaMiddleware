import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import data.preprocessing.StandardScaler;
import explore.Experiment;
import explore.ExperimentConfiguration;
import explore.user.DummyUser;
import explore.user.User;
import io.RunFileParser;
import io.TaskReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;

public class RunExperiment {
    enum Mode {
        NEW, RESUME, EVAL
    }

    /**
     * @param conditions array of boolean values
     * @return true if, and only if, all booleans are TRUE, or all booleans are FALSE
     */
    private static boolean areAllConditionsEqual(boolean... conditions) {
        if (conditions.length == 0)
            return true;

        boolean initial = conditions[0];
        for (boolean condition : conditions) {
            if (initial != condition) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Mode mode = Mode.RESUME;
        String folder = "experiment/sdss_Q1_0.1%/Simple Margin C=1000/";
        int[] fileIds = new int[]{1};

        for (int id : fileIds) {
            Path runFile = Paths.get(folder + id + ".run");
            Path outputFile = Paths.get(folder + id + ".eval");

            ExperimentConfiguration configuration;
            List<LabeledPoint> labeledPoints;

            try (BufferedReader reader = Files.newBufferedReader(runFile)) {
                RunFileParser parser = new RunFileParser(reader);
                configuration = parser.getExperimentConfiguration();
                labeledPoints = parser.getLabeledPoints();
            } catch (Exception ex) {
                //TODO: log error
                //TODO: catch parsing exceptions
                throw new RuntimeException("Failed to parse run file.", ex);
            }

            if (configuration == null) {
                throw new RuntimeException("Empty run file!");
            }

            if (!areAllConditionsEqual(mode == Mode.NEW, labeledPoints.isEmpty(), Files.notExists(outputFile))) {
                throw new RuntimeException("Incompatible mode and runs: mode is " + mode +
                        ", there are " + labeledPoints.size() + " labeled points, and .eval file "
                        + (Files.notExists(outputFile) ? "does not exist." : "exists"));
            }

            // get dataset and user
            //TODO: change configuration object to include detailed task info?
            String task = configuration.getTask();
            TaskReader reader = new TaskReader(task);

            List<DataPoint> dataPoints = StandardScaler.fitAndTransform(reader.readData());

            LabeledDataset labeledDataset = new LabeledDataset(dataPoints);
            labeledDataset.putOnLabeledSet(labeledPoints);

            Set<Long> positiveKeys = reader.readTargetSetKeys();
            User user = new DummyUser(positiveKeys);

            Experiment experiment = new Experiment.Builder(labeledDataset, user, configuration.getActiveLearner())
                    .subsample(configuration.getSubsampleSize())
                    .initialSampler(configuration.getInitialSampler())
                    .budget(configuration.getBudget())
                    .build();

            // run exploration
            try (BufferedWriter labeledPointsWriter = Files.newBufferedWriter(runFile, StandardOpenOption.APPEND);
                 BufferedWriter metricsWriter = Files.newBufferedWriter(outputFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                experiment.run(labeledPointsWriter, metricsWriter);

            } catch (Exception ex) {
                //TODO: log error
                ex.printStackTrace();
            }
        }
    }
}
