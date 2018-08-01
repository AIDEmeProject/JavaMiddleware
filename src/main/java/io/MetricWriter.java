package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MetricWriter {
    private BufferedWriter writer;

    public MetricWriter(String path) {
        try{
            this.writer = new BufferedWriter(new FileWriter(path, true));
        } catch (IOException ex){
            throw new RuntimeException("Failed to create metrics log file", ex);
        }
    }

    private void write(String line){
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException ex){
            throw new RuntimeException("Failed to write metrics to file", ex);
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException ex){
            throw new RuntimeException("Failed to close metrics log file", ex);
        }
    }

    public void write(IterationMetrics metrics){
        write(metrics.toString());
    }
}

