package explore.statistics;

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

    /**
     * Initializes the data structure with its name and value (equals to the mean).
     * @param name: metric's name
     * @param value: metric's initial value
     */
    public Statistics(String name, double value) {
        this(name, value, 0D, value, value, 1);
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
