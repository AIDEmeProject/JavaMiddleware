package utils.statistics;

/**
 * This class holds statistical information from a particular metric, such as its mean, variance, and number of samples.
 * As new values for the same metric are observed, its internal statistics can be updated as well.
 */
class Statistics {
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
     * Initializes the data structure with its name and value (equals to the mean).
     * @param name: metric's name
     * @param value: metric's initial value
     */
    public Statistics(String name, double value) {
        this.name = name;
        this.mean = value;
        this.variance = 0D;
        this.sampleSize = 1;
    }

    public String getName() {
        return name;
    }

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return sampleSize == 1 ? 1 : variance / (sampleSize - 1);
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
    }

    /**
     * @return a JSON string
     */
    @Override
    public String toString(){
        return "{\"metric\": \"" + name + "\", \"mean\": " + mean + ", \"var\": " + variance + ", \"n\": " + sampleSize + '}';
    }
}
