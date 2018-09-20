package data;

import machinelearning.classifier.Label;

import java.util.*;

public class PartitionedDataset {
    private List<DataPoint> points;
    private Map<Long, Integer> indexToPosition;
    private ExtendedLabel[] labels;
    private int modelStart;
    private int unknownStart;

    public PartitionedDataset(List<DataPoint> points) {
        this.points = new ArrayList<>(points);
        this.indexToPosition = new HashMap<>(points.size());
        this.labels = new ExtendedLabel[points.size()];
        this.modelStart = 0;
        this.unknownStart = 0;

        initializeDataStructures(points);
    }

    private void initializeDataStructures(List<DataPoint> points) {
        int i = 0;
        for (DataPoint point : points) {
            this.indexToPosition.put(point.getId(), i);
            labels[i++] = ExtendedLabel.UNKNOWN;
        }
    }

    public List<DataPoint> getAllPoints() {
        return points;
    }

    public List<LabeledPoint> getLabeledPoints() {
        List<LabeledPoint> labeledPoints = new ArrayList<>();
        for (int i = 0; i < modelStart; i++) {
            labeledPoints.add(new LabeledPoint(points.get(i), labels[i].toLabel()));
        }
        return labeledPoints;
    }

    public List<DataPoint> getUnlabeledPoints() {
        return points.subList(modelStart, points.size());
    }

    public List<DataPoint> getUncertainPoints() {
        return points.subList(unknownStart, points.size());
    }

    public ExtendedLabel getLabel(DataPoint point) {
        return labels[findPosition(point)];
    }

    public void update(DataPoint point, Label label) {
        int pos = findPosition(point);
        labels[pos] = ExtendedLabel.fromLabel(label);

        if(pos >= unknownStart) {
            swap(pos, unknownStart);
            swap(unknownStart++, modelStart++);
        }
        else if (pos >= modelStart) {
            swap(pos, modelStart++);
        }

        // TODO: update model
    }

    private void swap(int i, int j) {
        swapMapKeys(indexToPosition, points.get(i).getId(), points.get(j).getId());
        swapArrayElements(labels, i, j);
        Collections.swap(points, i, j);
    }

    private static <K, V> void swapMapKeys(Map<K, V> map, K key1, K key2) {
        V temp = map.get(key1);
        map.put(key1, map.get(key2));
        map.put(key2, temp);
    }

    private static <T> void swapArrayElements(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private int findPosition(DataPoint point) {
        Integer position = indexToPosition.get(point.getId());
        if (position == null) {
            throw new IllegalArgumentException("Point " + point + " not found.");
        }
        return position;
    }
}
