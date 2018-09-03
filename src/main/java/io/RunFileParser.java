package io;

import data.LabeledPoint;
import explore.ExperimentConfiguration;
import json.JsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RunFileParser {
    private ExperimentConfiguration experimentConfiguration;
    private List<LabeledPoint> labeledPoints;

    public RunFileParser(BufferedReader reader) throws IOException {
        this.experimentConfiguration = readExperimentConfiguration(reader);
        this.labeledPoints = readLabeledPoints(reader);
    }

    public ExperimentConfiguration getExperimentConfiguration() {
        return experimentConfiguration;
    }

    public List<LabeledPoint> getLabeledPoints() {
        return labeledPoints;
    }

    private List<LabeledPoint> readLabeledPoints(BufferedReader reader) throws IOException {
        List<LabeledPoint> labeledPoints = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            labeledPoints.addAll(JsonConverter.deserializeLabeledPoints(line));
        }

        return labeledPoints;
    }

    private ExperimentConfiguration readExperimentConfiguration(BufferedReader reader) throws IOException {
        return JsonConverter.deserialize(reader.readLine(), ExperimentConfiguration.class);
    }
}
