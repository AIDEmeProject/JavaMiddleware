package application.data;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import data.DataPoint;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;

public class CsvDatasetReader {

    static class Dataset{
        private List<String> columns;
    }

    public static void main(String[] args) throws IOException {
        try (

            Reader reader = Files.newBufferedReader(Paths.get("./src/main/java/application/data/example.csv"));
            CSVReader csvReader = new CSVReader(reader);

        ){

            List<String> columnNames = new ArrayList<String>();

            Dataset dataset = new Dataset();
            dataset.columns = new ArrayList<>();

            String[] columns = csvReader.readNext();

            for(String col : columns){
                dataset.columns.add(col);
            }

            Gson json = new Gson();

            System.out.println(json.toJson(dataset.columns));

        }
    }

    public String getColumnNames(File csvFile, char separator) throws IOException {


        Reader reader = Files.newBufferedReader(Paths.get(csvFile.getAbsolutePath()));
        CSVReader csvReader = new CSVReader(reader, separator);


        List<String> columnNames = new ArrayList<String>();

        Dataset dataset = new Dataset();
        dataset.columns = new ArrayList<>();

        String[] columns = csvReader.readNext();

        for(String col : columns){
            dataset.columns.add(col.trim());
        }

        Gson json = new Gson();

        return json.toJson(dataset.columns);

    }


    public void savedLabeledPointsAsCsv(ArrayList<DataPoint> labeledPoints){




    }
}
