package explore;

import data.LabeledPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * This class is a contains all metrics generated during an iteration of the exploration routine. Metrics can also be
 * encoded as JSON strings or decoded from them.
 */
public class Metrics {
    /**
     * Collection of (name, value) metrics
     */
    private Map<String, Double> metrics;

    /**
     * Collection of points and labels pooled in this iteration
     */
    private Collection<LabeledPoint> labeledPoints = Collections.emptyList();

    /**
     * Create empty metrics
     */
    public Metrics() {
        metrics = new HashMap<>();
    }

    /**
     * Set the collection of labeled points seen in this iteration
     * @param labeledPoints: collection of labeled points
     */
    public void setLabeledPoints(Collection<LabeledPoint> labeledPoints){
        this.labeledPoints = labeledPoints;
    }

    /**
     * Put a new metric in the internal storage. If there already is a metric with same name, its value will be overwritten.
     * @param name: metric's name
     * @param value: metric's value
     */
    public void put(String name, Double value){
        metrics.put(name, value);
    }

    /**
     * Put a collection of metrics in the internal storage
     * @param metrics: map metric_name to metric_value
     */
    public void putAll(Map<String, Double> metrics){
        for (Map.Entry<String, Double> entry : metrics.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param name: metric's name
     * @return value of the given metric
     * @throws IllegalArgumentException if name is not in collection
     */
    public Double get(String name){
        Double value = metrics.get(name);

        if (value == null){
            throw new IllegalArgumentException("Metric " + name + " not in collection.");
        }

        return value;
    }

    /**
     * @return collection of metric names
     */
    public Collection<String> names(){
        return metrics.keySet();
    }

    /**
     * @param json JSON string encoding a Metrics's object
     * @return new Metrics object
     */
    public static Metrics fromJson(String json){
        Metrics metrics = new Metrics();

        // decode json object
        JSONObject jsonObject = new JSONObject(json);

        // parse labeled points
        JSONArray points = (JSONArray) jsonObject.remove("points");
        Collection<LabeledPoint> labeledPoints = new ArrayList<>(points.length());

        for (Object point : points) {
            labeledPoints.add(LabeledPoint.fromJson(point.toString()));
        }

        metrics.setLabeledPoints(labeledPoints);

        // parse remaining metrics -> (String, Double) pairs
        for (String key : jsonObject.keySet()){
            metrics.put(key, jsonObject.getDouble(key));
        }

        return metrics;
    }

    /**
     * @return a JSON string encoding all the metrics and labeled points stored in this object
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // JSON opening bracket
        builder.append('{');

        // labeled points
        builder.append("\"points\": [");

        Iterator<LabeledPoint> labeledPointIterator = labeledPoints.iterator();

        if (labeledPointIterator.hasNext()){
            builder.append(labeledPointIterator.next().toString());

            while (labeledPointIterator.hasNext()){
                builder.append(',');
                builder.append(labeledPointIterator.next().toString());
            }
        }

        builder.append(']');

        // metrics
        for (Map.Entry<String, Double> entry : metrics.entrySet()){
            builder.append(", \"");
            builder.append(entry.getKey());
            builder.append("\": ");
            builder.append(entry.getValue());
        }

        // JSON closing bracket
        builder.append('}');

        return builder.toString();
    }
}
