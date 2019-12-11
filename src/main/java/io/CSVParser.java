package io;

import com.opencsv.CSVReader;
import data.DataPoint;
import data.IndexedDataset;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CSVParser{

    public IndexedDataset buildIndexedDataset(String filePath, ArrayList<Integer> columnIds, int keyColumnId) throws IOException{
        CSVReader reader = new CSVReader(new FileReader(filePath));
        reader.readNext();

        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        ValueParser parser = new ValueParser(columnIds.size());

        int rowNumber = 0;
        String [] nextLine;
        List<Long> secondaryIndex = new ArrayList<>();

        while ((nextLine = reader.readNext()) != null) {
            // read secondary index
            secondaryIndex.add(Long.parseLong(nextLine[keyColumnId]));

            // read data point
            double[] rowValues = new double[columnIds.size()];

            for (int i = 0; i < columnIds.size(); i++) {
                rowValues[i] = parser.parseValue(nextLine[columnIds.get(i)], i);
            }

            DataPoint dataPoint = new DataPoint(rowNumber, rowValues);
            builder.add(dataPoint);

            rowNumber++;
        }

        IndexedDataset dataset = builder.build();
        dataset.setSecondaryIndex(secondaryIndex);
        return dataset;
    }
}


