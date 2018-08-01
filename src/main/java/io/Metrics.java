package io;

import data.LabeledPoint;

import java.util.*;

public class Metrics {
    private Map<String, Double> metrics;
    private Collection<LabeledPoint> labeledPoints = Collections.emptyList();

    public Metrics() {
        metrics = new HashMap<>();
    }

    public void put(String name, Double value){
        metrics.put(name, value);
    }

    public void putAll(Map<String, Double> metrics){
        for (Map.Entry<String, Double> entry : metrics.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
    }

    public void setLabeledPoints(Collection<LabeledPoint> labeledPoints){
        this.labeledPoints = labeledPoints;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // JSON opening bracket
        builder.append('{');

        // labeled points
        builder.append("\"points\": [");

        Iterator<LabeledPoint> labeledPointIterator = labeledPoints.iterator();

        if (labeledPointIterator.hasNext()){
            builder.append(labeledPointIterator.next().toString());

            while (labeledPointIterator.hasNext()){
                builder.append(',');
                builder.append(labeledPointIterator.next().toString());
            }
        }

        builder.append(']');

        // metrics
        for (Map.Entry<String, Double> entry : metrics.entrySet()){
            builder.append(", \"");
            builder.append(entry.getKey());
            builder.append("\": ");
            builder.append(entry.getValue());
        }

        // JSON closing bracket
        builder.append('}');

        return builder.toString();
    }
}
