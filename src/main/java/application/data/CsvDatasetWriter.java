package application.data;

import com.opencsv.CSVWriter;
import data.LabeledPoint;
import explore.user.UserLabel;
import machinelearning.classifier.Label;
import utils.linalg.Vector;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

    public void savedLabeledPointsAsCsv(ArrayList<LabeledPoint> labeledPoints, String filePath) throws IOException {


        try (
                Writer writer = Files.newBufferedWriter(Paths.get(filePath));
                CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.RFC4180_LINE_END);
        ) {


            for (LabeledPoint point: labeledPoints) {

                point.getData();

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
