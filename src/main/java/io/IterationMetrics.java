package io;

import data.LabeledPoint;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class IterationMetrics{
    private Collection<LabeledPoint> labeledPoints = Collections.emptyList();
    private double getNextTimeMillis;
    private double userTimeMillis;
    private double fitTimeMillis;
    private double accuracyComputationTimeMillis;
    private double iterTimeMillis;

    public IterationMetrics() {
    }

    public void setLabeledPoints(Collection<LabeledPoint> labeledPoints){
        this.labeledPoints = labeledPoints;
    }

    public void setGetNextTimeMillis(double getNextTimeMillis) {
        this.getNextTimeMillis = getNextTimeMillis;
    }

    public void setUserTimeMillis(double userTimeMillis) {
        this.userTimeMillis = userTimeMillis;
    }

    public void setFitTimeMillis(double fitTimeMillis) {
        this.fitTimeMillis = fitTimeMillis;
    }

    public void setAccuracyComputationTimeMillis(double accuracyComputationTimeMillis) {
        this.accuracyComputationTimeMillis = accuracyComputationTimeMillis;
    }

    public void setIterTimeMillis(double iterTimeMillis) {
        this.iterTimeMillis = iterTimeMillis;
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

        // time measurements
        builder.append(", \"getNextTimeMillis\" : ");
        builder.append(getNextTimeMillis);

        builder.append(", \"userTimeMillis\" : ");
        builder.append(userTimeMillis);

        builder.append(", \"fitTimeMillis\" : ");
        builder.append(fitTimeMillis);

        builder.append(", \"accuracyComputationTimeMillis\" : ");
        builder.append(accuracyComputationTimeMillis);

        builder.append(", \"iterTimeMillis\" : ");
        builder.append(iterTimeMillis);

        // closing bracket
        builder.append('}');

        return builder.toString();
    }
}
