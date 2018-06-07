package explore;

import exceptions.IncompatibleMetricsException;
import exceptions.MetricAlreadyExistsException;
import exceptions.MetricNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This class is a simple storage of (String, Double) key-value pairs, where the String represents the metric's name and
 * the Double the metric's value. We provide Map-like methods for adding new metrics and retrieving keys, and also helper
 * methods for summing two metric objects and dividing their values by a given number.
 */
public class Metrics {
    /**
     * Collection of metrics to be stored
     */
    private final Map<String, Double> metrics;

    /**
     * Initialize an empty metric collection
     */
    public Metrics() {
        metrics = new HashMap<>();
    }

    /**
     * @return Number of metrics stored in this object
     */
    public int size(){
        return metrics.size();
    }

    /**
     * @return true if object contains no metrics
     */
    public boolean isEmpty(){
        return size() == 0;
    }

    /**
     * @param name: name of the metric to be retrieved
     * @return value of the metric with specified name.
     * @throws MetricNotFoundException if there isn't a metric of given name.
     */
    public Double get(String name){
        Double value = metrics.get(name);
        if (value == null){
            throw new MetricNotFoundException(name);
        }
        return value;
    }

    /**
     * Adds a new metric to our mapping
     * @param name: name of the new metric
     * @param value: value of the new metric
     * @throws MetricAlreadyExistsException if it already contains the given metric
     */
    public void add(String name, Double value){
        if (metrics.containsKey(name)){
            throw new MetricAlreadyExistsException(name);
        }
        metrics.put(name, value);
    }

    /**
     * Adds all metrics in 'metric' to the current object.
     * @param metric: metrics to be appended to this object
     * @throws MetricAlreadyExistsException if any of the metrics to be added is already contained in this object
     */
    public void addAll(Metrics metric){
        for (String key : metric.names()){
            this.add(key, metric.get(key));
        }
    }

    /**
     * Returns the names of all metrics stored in the current object. We do not guarantee that the order is the same as
     * that of insertion.
     * @return set of names
     */
    public Set<String> names(){
        return metrics.keySet();
    }

    /**
     * Static method for summing two metric objects.
     * @param metrics1: left hand side of sum
     * @param metrics2: right hand side of sum
     * @return new object containing the sum of the corresponding metrics
     * @throws IncompatibleMetricsException if objects do not contain the same metric names
     */
    public static Metrics sum(Metrics metrics1, Metrics metrics2){
        if ( !metrics1.names().equals(metrics2.names()) ){
            throw new IncompatibleMetricsException();
        }

        Metrics result = new Metrics();
        for (String key : metrics1.names()){
            result.add(key, metrics1.get(key) + metrics2.get(key));
        }
        return result;
    }

    /**
     * Instance method for summing another Metric object to this one. Equivalent to sum(this, metric).
     * @param metric: metric to sum to this object
     * @return a new object containing the sum.
     * @throws IncompatibleMetricsException if objects do not contain the same metric names
     */
    public Metrics sum(Metrics metric){
        return sum(this, metric);
    }

    /**
     * Static method dividing all metric values in an Metric object by a given number.
     * @param metric: object to be divided
     * @param denominator: number to divide each metric by
     * @return a new Metrics object containing the divided metrics at every position
     * @throws IllegalArgumentException if denominator is zero
     */
    public static Metrics divideByNumber(Metrics metric, int denominator){
        if (denominator == 0){
            throw new IllegalArgumentException("Dividing by zero.");
        }

        Metrics result = new Metrics();
        for (String key : metric.names()){
            result.add(key, metric.get(key) / denominator);
        }
        return result;
    }

    /**
     * Instance method for dividing each metric value by a number. Equivalent to divideByNumber(this, denominator).
     * @param denominator: number to divide each metric by
     * @return a new Metrics object containing the divided metrics
     * @throws IllegalArgumentException if denominator is zero
     */
    public Metrics divideByNumber(int denominator){
        return divideByNumber(this, denominator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metrics metrics1 = (Metrics) o;

        return metrics.equals(metrics1.metrics);
    }

    @Override
    public String toString() {
        return metrics.toString();
    }
}
