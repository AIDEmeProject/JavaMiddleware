package application.data;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import data.DataPoint;
import data.LabeledPoint;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvDatasetReader {

    protected double[] minimums;

    protected double[] maximums;

    protected String[] columns;

    protected ArrayList<HashSet<Double>> uniqueValues;

    static class Dataset{

        private List<String> columns;

        protected double[] minimums;

        protected double[] maximums;

        protected int[] uniqueValueNumbers;
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

    public String getCsvInfos(File csvFile, char separator) throws IOException {

        Reader reader = Files.newBufferedReader(Paths.get(csvFile.getAbsolutePath()));
        CSVReader csvReader = new CSVReader(reader, separator);

        List<String> columnNames = new ArrayList<String>();

        Dataset dataset = new Dataset();
        dataset.columns = new ArrayList<>();

        String[] columns = csvReader.readNext();
        this.uniqueValues = new ArrayList<HashSet<Double>>();

        for(String col : columns){
            dataset.columns.add(col.trim());
            HashSet<Double> set = new HashSet<Double>();

            uniqueValues.add(set);

        }

        this.maximums = new double[columns.length];
        this.minimums = new double[columns.length];

        String[] dataRow;
        int iRow = 1;
        while ((dataRow = csvReader.readNext()) != null) {
           this.parseInfo(dataRow, iRow);
           iRow++;
        }

        dataset.minimums = minimums;
        dataset.maximums = maximums;
        dataset.uniqueValueNumbers = this.computeUniqueValueNumbers();

        Gson json = new Gson();

        return json.toJson(dataset);
    }


    protected int[] computeUniqueValueNumbers(){
        int nCols = this.uniqueValues.size();

        int[] uniqueValueNumbers = new int[nCols];

        for (int iCol = 0; iCol < nCols ; iCol++ ){
            uniqueValueNumbers[iCol] = this.uniqueValues.get(iCol).size();
        }

        return uniqueValueNumbers;

    }

    protected void parseInfo(String[] dataRow, int iRow){

        double val = 0;

        for(int iCol = 0; iCol < dataRow.length; iCol++){

            val = Double.parseDouble(dataRow[iCol]);
            if (this.maximums[iCol] <= val){
                this.maximums[iCol] = val;
            }

            if (this.minimums[iCol] >= val){
                this.minimums[iCol] = val;
            }


            if (iRow < 100){
                this.uniqueValues.get(iCol).add(val);
            }
        }
    }
}
