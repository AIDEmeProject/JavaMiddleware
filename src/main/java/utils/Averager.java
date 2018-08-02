package utils;

import explore.Metrics;
import io.FolderManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This module is responsible for computing the average of all ".run" files in a folder.
 */
public class Averager {
    public static ArrayList<Metrics> computeAverage(FolderManager folder){
        File[] runFiles = folder.getRuns();

        ArrayList<Metrics> average = readMetrics(runFiles[0]);

        for (int i = 1; i < runFiles.length; i++) {
            mergeMetrics(average, readMetrics(runFiles[i]), i);
        }

        for (Metrics metrics : average){
            metrics.setLabeledPoints(Collections.EMPTY_LIST);
        }

        return average;
    }

    private static ArrayList<Metrics> readMetrics(File file){
        ArrayList<Metrics> metrics = new ArrayList<>();
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while((line = bufferedReader.readLine()) != null) {
                metrics.add(Metrics.fromJson(line));
            }
        }
        catch(FileNotFoundException ex) {
            throw new RuntimeException("File not found: " + file, ex);
        }
        catch(IOException ex) {
            throw new RuntimeException("IO error while reading file: " + file, ex);
        }

        return metrics;
    }

    private static void mergeMetrics(ArrayList<Metrics> averages, ArrayList<Metrics> metrics, int size){
        Validator.assertEquals(averages.size(), metrics.size());

        size++;

        for (int i = 0; i < averages.size(); i++) {
            Metrics average = averages.get(i), metric = metrics.get(i);

            for (String name : average.names()){
                Double diff = (metric.get(name) - average.get(name)) / size;
                average.put(name, average.get(name) + diff);
            }
        }
    }
}
