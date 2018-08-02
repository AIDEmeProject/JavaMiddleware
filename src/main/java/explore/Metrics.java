package explore;

import data.LabeledPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * This class is a contains all metrics generated during an iteration of the exploration routine. In addition, it can parse
 * all metrics into a valid JSON string.
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

    public Metrics() {
        metrics = new HashMap<>();
    }

    public static Metrics fromJson(String json){
        Metrics metrics = new Metrics();

        // decode json object
        JSONObject jsonObject = new JSONObject(json);

        // parse labeled points
        JSONArray points = (JSONArray) jsonObject.remove("points"); // jsonObject.getJSONArray("points");
        Collection<LabeledPoint> labeledPoints = new ArrayList<>(points.length());

        for (int i = 0; i < points.length(); i++) {
            labeledPoints.add(LabeledPoint.fromJson(points.getJSONObject(i)));
        }

        metrics.setLabeledPoints(labeledPoints);

        // parse remaining metrics -> (String, Double) pairs
        for (String key : jsonObject.keySet()){
            metrics.put(key, jsonObject.getDouble(key));
        }

        return metrics;
    }

    public Collection<String> names(){
        return metrics.keySet();
    }

    public Double get(String name){
        return metrics.get(name);
    }

    /**
     * Put a new metric in the internal storage.
     * @param name: metric's name
     * @param value: metric's value
     */
    public void put(String name, Double value){
        metrics.put(name, value);
    }

    /**
     * Put a collection of metrics in the internal storage
     * @param metrics: map metric_name -> metric_value
     */
    public void putAll(Map<String, Double> metrics){
        for (Map.Entry<String, Double> entry : metrics.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set the collection of labeled points seen in this iteration
     * @param labeledPoints: collection of labeled points
     */
    public void setLabeledPoints(Collection<LabeledPoint> labeledPoints){
        this.labeledPoints = labeledPoints;
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
