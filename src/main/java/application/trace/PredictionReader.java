package application.trace;

import data.IndexedDataset;
import data.LabeledPoint;
import machinelearning.classifier.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class PredictionReader {
    private final static String FOLDER = "/Users/luciano/Desktop/traces/jobs/";
    private final static Pattern splitPattern = Pattern.compile(",\\s*");
    private final Label[][] allLabels;

    public PredictionReader(String algorithm) {
        this.allLabels = read(algorithm);
    }

    public List<LabeledPoint> getPredictionsForIteration(IndexedDataset dataset, int i) {
        Label[] labels = allLabels[i];

        List<LabeledPoint> points = new ArrayList<>(dataset.length());
        for (int j = 0; j < labels.length; j++) {
            points.add(new LabeledPoint(dataset.get(j), labels[j]));
        }
        return points;
    }

    private Label[][] read(String algorithm) {
        Path path = Paths.get(FOLDER, algorithm, "predictions.txt");

        try(Stream<String> lines = Files.lines(path)) {
            return lines.map(PredictionReader::parseLine).toArray(Label[][]::new);
        } catch (IOException ex) {
            throw new RuntimeException("IO error while reading predictions file.", ex);
        }
    }

    private static Label[] parseLine(String line) {
        return splitPattern.splitAsStream(line)
                .map(Double::parseDouble)
                .map(Label::fromSign)
                .toArray(Label[]::new);
    }
}
