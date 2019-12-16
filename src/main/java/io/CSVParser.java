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


