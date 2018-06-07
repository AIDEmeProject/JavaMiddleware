package explore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IterationMetrics {
    private Map<String, Double> metrics;

    public IterationMetrics() {
        metrics = new HashMap<>();
    }

    public Double get(String name){
        Double value = metrics.get(name);
        if (value == null){
            throw new RuntimeException("Object does not contain the requested metric: " + name);
        }
        return value;
    }

    public void add(String name, Double value){
        if (metrics.containsKey(name)){
            throw new RuntimeException("Trying to include the same metric twice.");
        }
        metrics.put(name, value);
    }

    public void addAll(Map<String, Double> map){
        for (String key : map.keySet()){
            this.add(key, map.get(key));
        }
    }

    public Set<String> names(){
        return metrics.keySet();
    }

    public static IterationMetrics sum(IterationMetrics map1, IterationMetrics map2){
        IterationMetrics result = new IterationMetrics();
        for (String key : map1.names()){
            result.add(key, map1.get(key) + map2.get(key));
        }
        return result;
    }

    public IterationMetrics sum(IterationMetrics map){
        return sum(this, map);
    }

    public static IterationMetrics divideByNumber(IterationMetrics map, int denominator){
        if (denominator == 0){
            throw new IllegalArgumentException("Dividing by zero.");
        }

        IterationMetrics result = new IterationMetrics();
        for (String key : map.names()){
            result.add(key, map.get(key) / denominator);
        }
        return result;
    }

    public IterationMetrics divideByNumber(int denominator){
        return divideByNumber(this, denominator);
    }

    @Override
    public String toString() {
        return metrics.toString();
    }
}
