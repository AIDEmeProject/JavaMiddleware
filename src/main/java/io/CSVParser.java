package io;


import com.opencsv.CSVReader;
import data.DataPoint;
import data.IndexedDataset;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CSVParser{
    public IndexedDataset buildIndexedDataset(String filePath, Map<String, String[]> postData)
            throws IOException {

        Reader in = new FileReader(filePath);
        CSVReader reader = new CSVReader(in);

        Integer columnId;

        ArrayList<Integer> columnIds = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : postData.entrySet()){

            if (entry.getKey().indexOf("column") != -1){

                String strColumnId = String.join(",", entry.getValue());

                columnId = Integer.parseInt(strColumnId);

                columnIds.add(columnId);
            }
        }

        columnIds.sort(Comparator.comparingInt((Integer n) -> n));

        ArrayList<Double> rowValues = new ArrayList();
        ArrayList<String> rowRawValues = new ArrayList();


        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        Integer rowNumber = 0;
        String [] nextLine;

        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        reader.readNext();

        ValueParser parser = new ValueParser((columnIds.size()));
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line

            rowValues.removeAll(rowValues);
            rowRawValues.removeAll(rowRawValues);
            int i = 0;
            for (Integer id : columnIds){


                String rawValue = nextLine[id];
                Double value = parser.parseValue(nextLine[id], i);

                rowValues.add(value);
                rowRawValues.add(rawValue);
                i++;
            }

            double[] doubleRowValues = this.doubleConversion(rowValues);
            DataPoint dataPoint = new DataPoint(rowNumber, doubleRowValues);

            dataPoints.add(dataPoint);

            builder.add(dataPoint);
            rowNumber++;
        }

        IndexedDataset dataset = builder.build();
        return dataset;
    }

    public double[] doubleConversion(ArrayList<Double> values){

        double[] convertedValues = new double[values.size()];
        int index = 0;
        for(Double value: values){

            convertedValues[index] = value;
            index++;
        }

        return convertedValues;
    }
}




class ValueParser{
    protected int nColumns;

    protected ArrayList<HashMap<String, Integer>> columns;

    public ValueParser(int nColumns){

        this.nColumns = nColumns;
        this.columns = new ArrayList<HashMap<String, Integer>>();

        for (int i = 0; i<nColumns; i++){
            columns.add(new HashMap<String, Integer>());
        }
    }

    public double parseValue(String data, int iColumn){
        double val;
        try{
            val = Double.parseDouble(data);
        }
        catch (NumberFormatException e){
            val = this.getCategory(data, iColumn);
        }
        return val;
    }

    protected int getCategory(String data, int iColumn){

        HashMap<String, Integer> cat = this.columns.get(iColumn);
        int newCategory = cat.size();

        if (! cat.containsKey(data)){
            cat.put(data, newCategory);
            return newCategory;
        }
        return cat.get(data);
    }

}