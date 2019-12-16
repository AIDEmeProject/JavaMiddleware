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

import java.util.HashSet;
import java.util.Objects;

/**
 * This class holds statistical information from a particular metric, such as its mean, variance, and number of samples.
 * As new values for the same metric are observed, its internal statistics can be updated as well.
 */
public class Statistics {
    /**
     * Metric's name
     */
    private String name;

    /**
     * Metric's mean
     */
    private double mean;

    /**
     * Metric's variance
     */
    private double variance;

    /**
     * Number of samples
     */
    private int sampleSize;

    /**
     * Metric's minimum value
     */
    private double min;

    /**
     * Metric's maximum value
     */
    private double max;

    private HashSet<Double> uniqueValues;

    /**
     * Initializes the data structure with its name and value (equals to the mean).
     * @param name: metric's name
     * @param value: metric's initial value
     */
    public Statistics(String name, double value) {

        this(name, value, 0D, value, value, 1);

        this.uniqueValues = new HashSet<>();
        this.uniqueValues.add(value);
    }

    public Statistics(String name, double mean, double variance, double min, double max, int sampleSize) {
        this.name = name;
        this.mean = mean;
        this.variance = variance;
        this.sampleSize = sampleSize;
        this.min = min;
        this.max = max;


    }

    public String getName() {
        return name;
    }

    public double getSum() {
        return mean * sampleSize;
    }

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return sampleSize == 1 ? 0.0 : variance / sampleSize;
    }

    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    public double getMinimum() {
        return min;
    }

    public double getMaximum() {
        return max;
    }

    public boolean isNumeric(){

        System.out.println("unique values and sample size");
        System.out.println(this.uniqueValues.size());
        System.out.println(this.sampleSize);
        System.out.println(0.8 * this.sampleSize);

        boolean hasMoreThan100UniqueValues = this.uniqueValues.size() > 100;

        return hasMoreThan100UniqueValues || ((double) this.uniqueValues.size() > 0.8 * this.sampleSize);

    }

    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Updates internal mean and variance given a new value for this metric.
     * @param value: new observed value for this metric
     */
    public void update(double value){
        double diff = value - mean;

        sampleSize++;
        mean += diff / sampleSize;
        variance += diff * (value - mean);
        min = Math.min(min, value);
        max = Math.max(max, value);
        uniqueValues.add(value);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return Double.compare(that.mean, mean) == 0 &&
                Double.compare(that.variance, variance) == 0 &&
                sampleSize == that.sampleSize &&
                min == that.min &&
                max == that.max &&
                Objects.equals(name, that.name);
    }

    /**
     * @return a JSON string
     */
    @Override
    public String toString(){
        return "{\"metric\": \"" + name + "\", \"mean\": " + mean + ", \"std\": " + getStandardDeviation() +
                ", \"min\": " + min + ", \"max\": " + max +
                ", \"n\": " + sampleSize + '}';
    }
}
