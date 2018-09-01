import data.LabeledDataset;
import data.LabeledPoint;
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
    public static void main(String[] args) {
        Path runFile = Paths.get("experiment/sdss_Q1_0.1%/Simple Margin C=1000/test.run");
        Path outputFile = Paths.get("experiment/sdss_Q1_0.1%/Simple Margin C=1000/test.eval");

        ExperimentConfiguration configuration;
        List<LabeledPoint> labeledPoints;

        try(BufferedReader reader = Files.newBufferedReader(runFile)) {
            RunFileParser parser = new RunFileParser(reader);
            configuration = parser.getExperimentConfiguration();
            labeledPoints = parser.getLabeledPoints();
        }
        catch (Exception ex) {
            //TODO: log error
            //TODO: catch parsing exceptions
            throw new RuntimeException("Failed to parse run file.", ex);
        }

        if (configuration == null) {
            throw new RuntimeException("Empty file");
        }

        System.out.println(configuration);
        System.out.println(labeledPoints);

        // get dataset and user
        //TODO: change configuration object to include detailed task info?
        String task = configuration.getTask();
        TaskReader reader = new TaskReader(task);

        LabeledDataset labeledDataset = new LabeledDataset(reader.readData());
        labeledDataset.putOnLabeledSet(labeledPoints);

        Set<Long> positiveKeys = reader.readTargetSetKeys();
        User user = new DummyUser(positiveKeys);

        Experiment.Builder builder = new Experiment.Builder(labeledDataset, user, configuration.getActiveLearner())
                .subsample(configuration.getSubsampleSize())
                .initialSampler(configuration.getInitialSampler())
                .budget(configuration.getBudget());

        // run exploration
        try (BufferedWriter labeledPointsWriter = Files.newBufferedWriter(runFile, StandardOpenOption.APPEND);
             BufferedWriter metricsWriter = Files.newBufferedWriter(outputFile, StandardOpenOption.APPEND)) {

            builder.build().run(labeledPointsWriter, metricsWriter);

        } catch (Exception ex){
            //TODO: log error
            ex.printStackTrace();
        }
    }
}
