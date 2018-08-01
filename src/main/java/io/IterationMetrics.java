package io;

import data.LabeledPoint;

import java.util.*;

public class IterationMetrics{
    private Collection<LabeledPoint> labeledPoints = Collections.emptyList();
    private Map<String, Double> metrics;

    public IterationMetrics() {
        metrics = new HashMap<>();
    }

    public void setLabeledPoints(Collection<LabeledPoint> labeledPoints){
        this.labeledPoints = labeledPoints;
    }

    public void add(String name, Double value){
        metrics.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // opening bracket
        builder.append('{');

        // labeled points
        builder.append("\"points\" : [");

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
            builder.append("\" : ");
            builder.append(entry.getValue());
        }

        // closing bracket
        builder.append('}');

        return builder.toString();
    }
}
