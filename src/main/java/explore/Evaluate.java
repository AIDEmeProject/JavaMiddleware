package explore;

import data.DataPoint;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.metrics.MetricCalculator;
import explore.user.User;
import io.FolderManager;
import io.json.JsonConverter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public final class Evaluate {
    private FolderManager folder;
    private User user;
    private List<DataPoint> dataPoints;

    public Evaluate(FolderManager folder, List<DataPoint> dataPoints, User user) {
        this.folder = folder;
        this.user = user;
        this.dataPoints = dataPoints;
    }

    public void evaluate(int id, String calculatorIdentifier) {
        MetricCalculator metricCalculator = folder.getMetricCalculator(calculatorIdentifier);

        PartitionedDataset partitionedDataset = new PartitionedDataset(dataPoints);

        Path evalFile = folder.getEvalFile(calculatorIdentifier, id);

        try (BufferedWriter evalFileWriter = Files.newBufferedWriter(evalFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            long iter = 0;
            long numberOfPreviousIters = countLinesOfFile(evalFile);

            for (List<LabeledPoint> labeledPoints : folder.getLabeledPoints(id)) {
                partitionedDataset.update(labeledPoints);

                if (iter++ < numberOfPreviousIters) {
                    continue;
                }

                Map<String, Double> metrics = metricCalculator.compute(partitionedDataset, user).getMetrics();

                writeLineToFile(evalFileWriter, JsonConverter.serialize(metrics));
            }
        } catch (IOException ex) {
            throw new RuntimeException("evaluation failed.", ex);
        }
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