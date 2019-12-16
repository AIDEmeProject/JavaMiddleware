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
