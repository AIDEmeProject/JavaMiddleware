package utils.statistics;

import explore.Metrics;
import io.MultipleFilesReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * This module is responsible for parsing all ".run" files in a folder, aggregating all metrics into useful statistics
 * such as mean and variance. Files are NOT required to have the same number of lines or the exactly same metrics.
 */
public class StatisticsCalculator {

    /**
     * This method will parse an array of "run" files and will aggregate the metrics of each line into useful statistics,
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

        for (String line : lines) {
            collection.update(Metrics.fromJson(line));
        }

        return collection;
    }
}
