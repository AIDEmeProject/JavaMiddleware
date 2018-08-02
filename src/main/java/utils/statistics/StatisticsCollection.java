package utils.statistics;

import explore.Metrics;

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
     * Adds a new statistic. If the collection already contains a statistic with the same name, it will be overwritten.
     * @param statistics: new statistic to insert
     */
    public void add(Statistics statistics){
        this.statistics.put(statistics.getName(), statistics);
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

    public void update(Metrics metrics){
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
