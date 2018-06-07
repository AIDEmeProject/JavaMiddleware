package explore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Metrics {
    private Map<String, Double> metrics;

    public Metrics() {
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

    public void addAll(Metrics metric){
        for (String key : metric.names()){
            this.add(key, metric.get(key));
        }
    }

    public Set<String> names(){
        return metrics.keySet();
    }

    public static Metrics sum(Metrics map1, Metrics map2){
        Metrics result = new Metrics();
        for (String key : map1.names()){
            result.add(key, map1.get(key) + map2.get(key));
        }
        return result;
    }

    public Metrics sum(Metrics metric){
        return sum(this, metric);
    }

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

    public Metrics divideByNumber(int denominator){
        return divideByNumber(this, denominator);
    }

    @Override
    public String toString() {
        return metrics.toString();
    }
}
