package user;

import data.DataPoint;
import data.LabeledPoint;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An User represents the "oracle" of Active Learning scenario: an expert or human annotator capable of, given a data point,
 * return whether it is a positive (i.e. 1) or a negative (i.e. 0) label.
 */
public interface User {
    /**
     * Given a dataset and a row, return the label of data[row]
     * @param point: point to label
     * @return label of data[row]
     */
    int getLabel(DataPoint point);

    /**
     * Return the labels of a batch of rows
     * @param points: collection of data points
     * @return an array containing the labels of each requested row
     */
    default int[] getLabel(Collection<DataPoint> points){
        int[] labels = new int[points.size()];

        int i = 0;
        for (DataPoint point : points){
            labels[i++] = getLabel(point);
        }

        return labels;
    }

    default LabeledPoint getLabeledPoint(DataPoint point){
        return new LabeledPoint(point, getLabel(point));
    }

    default Collection<LabeledPoint> getLabeledPoint(Collection<DataPoint> points){
        Collection<LabeledPoint> labeledPoints = new ArrayList<>(points.size());

        for (DataPoint point : points){
            labeledPoints.add(getLabeledPoint(point));
        }

        return labeledPoints;
    }
}
