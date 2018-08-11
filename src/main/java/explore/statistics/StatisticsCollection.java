package explore.statistics;

import explore.IterationMetrics;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * This class maintains a collection of metric statistics, allowing to add new statistics and update their values.
 */
class StatisticsCollection {
    /**
     * mapping metric_name -> statistics
     */
    private Map<String, Statistics> statistics;

    /**
     * Creates an empty collection
     */
    public StatisticsCollection() {
        statistics = new HashMap<>();
    }

    /**
     * @param name: statistic's name
     * @return requested statistic
     * @throws IllegalArgumentException if name is not in collection
     */
    public Statistics get(String name){
        Statistics stat = statistics.get(name);

        if (stat == null){
            throw new IllegalArgumentException("Statistic " + name + " not in collection.");
        }

        return stat;
    }

    /**
     * Update's a particular metric statistic with a new value. If this metric is not in the object, it will be inserted instead.
     *
     * @param name: name of metric to be updated
     * @param value: new value observed for this metric
     */
    public void update(String name, Double value){
        if (this.statistics.containsKey(name)){
            this.statistics.get(name).update(value);
        }
        else {
            statistics.put(name, new Statistics(name, value));
        }
    }

    /**
     * Update statistics from values in Metrics object. Metrics not in this collection will be simply appended.
     * @param metrics: Metrics object
     */
    public void update(IterationMetrics metrics){
        for (String name : metrics.names()){
            update(name, metrics.get(name));
        }
    }

    /**
     * @return JSON array of all statistics in this collection
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ","[", "]");
        for (Statistics metric : statistics.values()){
            joiner.add(metric.toString());
        }
        return joiner.toString();
    }
}
