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
                CSVWriter csvWriter = new CSVWriter(writer);
        ) {


            for (LabeledPoint point: labeledPoints) {

                point.getData();

                String[] strings = new String[point.getData().length() + 2];


                strings[0] = String.valueOf(point.getId());
                Vector data = point.getData();

                for (int i = 0; i < data.length(); i++){
                    strings[i + 1] = String.valueOf(data.get(i));
                }
                strings[strings.length - 1] = point.getLabel().toString();
                csvWriter.writeNext(strings);
            }
        }

    }
}
