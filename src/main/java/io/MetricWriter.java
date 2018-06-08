package io;

import explore.ExplorationMetrics;
import explore.Metrics;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

public class MetricWriter {
    public static void write(ExplorationMetrics metrics, String path) {
        Collection<String> namesCollection = metrics.getNames();
        String[] names = namesCollection.toArray(new String[namesCollection.size()]);

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(names))  //TODO: add header
        ) {
            Iterator<Metrics> it = metrics.iterator();
            while (it.hasNext()){
                Metrics line = it.next();
                csvPrinter.printRecord(line.values());
            }

            csvPrinter.flush();
        }
        catch (IOException ex){
            ex.printStackTrace();
            throw new RuntimeException("Couldn't write metrics to file.");
        }
    }

}
