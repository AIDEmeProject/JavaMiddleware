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

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import data.DataPoint;
import data.LabeledPoint;
import io.CSVParser;
import io.ValueParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvDatasetReader {

    protected double[] minimums;

    protected double[] maximums;

    protected String[] columns;

    protected boolean[] hasFloats;

    protected ArrayList<HashSet<Double>> uniqueValues;

    static class Dataset{

        private List<String> columns;

        protected double[] minimums;

        protected double[] maximums;

        protected int[] uniqueValueNumbers;

        protected boolean[] hasFloats;

        int nRows;
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
        this.hasFloats = new boolean[columns.length];

        String[] dataRow;
        ValueParser parser = new ValueParser(columns.length);

        int iRow = 1;
        while ((dataRow = csvReader.readNext()) != null) {
           this.parseInfo(dataRow, iRow, parser);
           iRow++;
        }

        dataset.minimums = minimums;
        dataset.maximums = maximums;
        dataset.uniqueValueNumbers = parser.getUniqueValueCount();
        dataset.hasFloats = this.hasFloats;
        dataset.nRows = iRow;

        Gson json = new Gson();

        return json.toJson(dataset);
    }



    protected void parseInfo(String[] dataRow, int iRow, ValueParser parser){

        int nCol = dataRow.length;


        double val = 0;

        for(int iCol = 0; iCol < nCol; iCol++){

            val = parser.parseValue(dataRow[iCol], iCol);

            if (this.maximums[iCol] <= val){
                this.maximums[iCol] = val;
            }

            if (this.minimums[iCol] >= val){
                this.minimums[iCol] = val;
            }

            if (val != (int) val){
                this.hasFloats[iCol] = true;
            }


            this.uniqueValues.get(iCol).add(val);

        }
    }


    protected int[] computeUniqueValueNumbers(){

        int nCols = this.uniqueValues.size();

        int[] uniqueValueNumbers = new int[nCols];

        for (int iCol = 0; iCol < nCols ; iCol++ ){
            uniqueValueNumbers[iCol] = this.uniqueValues.get(iCol).size();
        }

        return uniqueValueNumbers;

    }


}
