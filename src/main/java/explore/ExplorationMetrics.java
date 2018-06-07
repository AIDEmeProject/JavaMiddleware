package explore;

import java.util.*;

/**
 * This class holds all metrics computed during the exploration process. It also provides helper methods for summing metrics
 * and normalizing values by a constant.
 */
public class ExplorationMetrics {
    /**
     * List of metrics retrieved at each iteration of the exploration process
     */
    private List<Metrics> metrics;

    /**
     * Initializes an empty list of metrics
     */
    public ExplorationMetrics(){
        this.metrics = new ArrayList<>();
    }

    /**
     * Append a new metric to the end of the list.
     * @param metric: metrics to be appended
     */
    public void add(Metrics metric){
        metrics.add(metric);
    }

    /**
     * Static method performing the sum of two ExplorationMetric objects.
     * @param metrics1: first argument to sum
     * @param metrics2: second argument to sum
     * @return a new ExplorationMetric object containing the sum of corresponding metrics at every position
     * @throws IllegalArgumentException if objects have a different number of elements
     */
    public static ExplorationMetrics sum(ExplorationMetrics metrics1, ExplorationMetrics metrics2){
        ExplorationMetrics result = new ExplorationMetrics();
        Iterator<Metrics> it1 = metrics1.iterator();
        Iterator<Metrics> it2 = metrics2.iterator();

        while (it1.hasNext() && it2.hasNext()){
            result.add(Metrics.sum(it1.next(), it2.next()));
        }

        // if lists have different lengths, one of the iterators will still have elements remaining
        if (it1.hasNext() || it2.hasNext()){
            throw new IllegalArgumentException("Lists should have the same number of elements.");
        }

        return result;
    }

    /**
     * Instance method for summing another ExplorationMetric to the current metric object. Equivalent to sum(this, metrics).
     * @param metrics: object to sum
     * @return a new ExplorationMetric object containing the sum of metrics
     * @throws IllegalArgumentException if objects have a different number of elements
     */
    public ExplorationMetrics sum(ExplorationMetrics metrics){
        return sum(this, metrics);
    }

    /**
     * Static method dividing all metrics in an ExplorationMetric object by a given number.
     * @param metrics: object to be divided
     * @param denominator: number to divide each metric by
     * @return a new ExplorationMetric object containing the divided metrics at every position
     */
    public static ExplorationMetrics divideByNumber(ExplorationMetrics metrics, int denominator){
        ExplorationMetrics result = new ExplorationMetrics();

        for (Metrics map : metrics.metrics){
            result.add(map.divideByNumber(denominator));
        }

        return result;
    }

    /**
     * Instance method for dividing the current metric object by a number. Equivalent to divideByNumber(this, denominator).
     * @param denominator: number to divide each metric by
     * @return a new ExplorationMetric object containing the divided metrics at every position
     */
    public ExplorationMetrics divideByNumber(int denominator){
        return divideByNumber(this, denominator);
    }

    /**
     * @return an iterator to the Metrics contained in this object. Elements are retrieved in the same order of insertion.
     */
    public Iterator<Metrics> iterator(){
        return metrics.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Metrics map : metrics){
            builder.append(map.toString());
            builder.append('\n');
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
