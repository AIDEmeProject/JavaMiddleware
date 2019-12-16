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

package explore.statistics;

import io.MultipleFilesReader;
import io.json.JsonConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * This module is responsible for parsing all ".run" files in a folder, aggregating all metrics  into useful statistics
 * such as mean and variance. Files are NOT required to have the same number of lines or the exactly same metrics .
 */
public class StatisticsCalculator {

    /**
     * This method will parse an array of "run" files and will aggregate the metrics  of each line into useful statistics,
     * outputting results in a new output file.
     *
     * @param runs: run files to average
     * @param output: file to save average output
     */
    public static void averageRunFiles(File[] runs, File output){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output));
             MultipleFilesReader reader = new MultipleFilesReader(runs)) {

            while (reader.hasNext()){
                writer.write(aggregateLineMetrics(reader.readlines()).toString());
                writer.newLine();
            }
        }
        catch (IOException ex){
            throw new RuntimeException("IO error while reading run files or writing statistics.", ex);
        }
    }

    private static StatisticsCollection aggregateLineMetrics(Collection<String> lines){
        StatisticsCollection collection = new StatisticsCollection();
        lines.forEach(line -> collection.update(JsonConverter.deserializeMetricsMap(line)));
        return collection;
    }
}
