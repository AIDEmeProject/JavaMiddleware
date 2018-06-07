package explore;

import java.util.*;

/**
 * This class holds all metrics computed during the exploration process. It also provides helper methods for summing metrics
 * and normalizing values by a constant.
 */
public class ExplorationMetrics {
    private List<Map<String, Double>> metrics;

    public ExplorationMetrics(){
        this.metrics = new ArrayList<>();
    }

    private ExplorationMetrics(List<Map<String, Double>> metrics) {
        this.metrics = metrics;
    }

    public List<Map<String, Double>> getMetrics() {
        return metrics;
    }

    public void add(Map<String, Double> map){
        metrics.add(map);
    }

    public static ExplorationMetrics sum(ExplorationMetrics metrics1, ExplorationMetrics metrics2){
        List<Map<String, Double>> result = new ArrayList<>();
        Iterator<Map<String, Double>> it1 = metrics1.metrics.iterator();
        Iterator<Map<String, Double>> it2 = metrics2.metrics.iterator();

        while (it1.hasNext() && it2.hasNext()){
            result.add(sumMaps(it1.next(), it2.next()));
        }

        if (it1.hasNext() || it2.hasNext()){
            throw new IllegalArgumentException("Lists should have the same number of elements.");
        }

        return new ExplorationMetrics(result);
    }

    public static ExplorationMetrics divideByNumber(ExplorationMetrics metrics, int denominator){
        List<Map<String, Double>> result = new ArrayList<>();

        for (Map<String, Double> map : metrics.metrics){
            result.add(divideMap(map, denominator));
        }

        return new ExplorationMetrics(result);
    }

    private static Map<String, Double> sumMaps(Map<String, Double> map1, Map<String, Double> map2){
        if (!map1.keySet().equals(map2.keySet())){
            throw new IllegalArgumentException("Maps must have the same key set.");
        }

        Map<String, Double> result = new HashMap<>();
        for (String key : map1.keySet()){
            result.put(key, map1.get(key) + map2.get(key));
        }
        return result;
    }

    private static Map<String, Double> divideMap(Map<String, Double> map, int denominator){
        if (denominator == 0){
            throw new IllegalArgumentException("Dividing by zero.");
        }

        Map<String, Double> result = new HashMap<>();

        for (String key : map.keySet()){
            result.put(key, map.get(key) / denominator);
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map<String, Double> map : metrics){
            builder.append(map);
            builder.append('\n');
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
