package explore;

import java.util.*;

/**
 * This class holds all metrics computed during the exploration process. It also provides helper methods for summing metrics
 * and normalizing values by a constant.
 */
public class ExplorationMetrics {
    private List<Metrics> metrics;

    public ExplorationMetrics(){
        this.metrics = new ArrayList<>();
    }

    public void add(Metrics map){
        metrics.add(map);
    }

    public static ExplorationMetrics sum(ExplorationMetrics metrics1, ExplorationMetrics metrics2){
        ExplorationMetrics result = new ExplorationMetrics();
        Iterator<Metrics> it1 = metrics1.iterator();
        Iterator<Metrics> it2 = metrics2.iterator();

        while (it1.hasNext() && it2.hasNext()){
            result.add(Metrics.sum(it1.next(), it2.next()));
        }

        if (it1.hasNext() || it2.hasNext()){
            throw new IllegalArgumentException("Lists should have the same number of elements.");
        }

        return result;
    }

    public ExplorationMetrics sum(ExplorationMetrics metrics){
        return sum(this, metrics);
    }

    public static ExplorationMetrics divideByNumber(ExplorationMetrics metrics, int denominator){
        ExplorationMetrics result = new ExplorationMetrics();

        for (Metrics map : metrics.metrics){
            result.add(map.divideByNumber(denominator));
        }

        return result;
    }

    public ExplorationMetrics divideByNumber(int denominator){
        return divideByNumber(this, denominator);
    }

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
