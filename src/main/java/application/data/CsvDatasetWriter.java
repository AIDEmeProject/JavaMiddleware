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

package application.data;

import com.opencsv.CSVWriter;
import data.LabeledPoint;
import machinelearning.classifier.Label;
import utils.linalg.Vector;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvDatasetWriter {

    public static void main(String[] args) throws IOException {

        long id = 1;
        double[] data = {2, 3};
        Label label = Label.fromSign(1);
        LabeledPoint point = new LabeledPoint(id, data, label);
        ArrayList<LabeledPoint> labeledPoints = new ArrayList<>();
        labeledPoints.add(point);
        labeledPoints.add(point);
        labeledPoints.add(point);

        CsvDatasetWriter writer = new CsvDatasetWriter();

        writer.savedLabeledPointsAsCsv(labeledPoints, "test.csv");

    }

    public void savedLabeledPointsAsCsv(List<LabeledPoint> labeledPoints, String filePath) throws IOException {


        try (
                Writer writer = Files.newBufferedWriter(Paths.get(filePath));
                CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.RFC4180_LINE_END);
        ) {


            for (LabeledPoint point: labeledPoints) {


                String[] strings = new String[point.getData().length() + 2];


                strings[0] = String.valueOf(point.getId());
                Vector data = point.getData();

                for (int i = 0; i < data.length(); i++){
                    strings[i + 1] = String.valueOf(data.get(i));
                }

                String label = Integer.toString(point.getLabel().asSign());
                strings[strings.length - 1] = label;
                csvWriter.writeNext(strings);
            }
        }

    }
}
