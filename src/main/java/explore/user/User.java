package explore.user;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An User represents the "oracle" of Active Learning scenario, i.e. a human annotator capable of, given a {@link DataPoint},
 * return whether it is represents a POSITIVE or a NEGATIVE {@link Label}.
 */
public interface User {
    /**
     * Given a dataset and a row, return the label of data[row]
     * @param point: point to label
     * @return label of data[row]
     */
    Label getLabel(DataPoint point);

    /**
     * Return the labels of a batch of rows
     * @param points: collection of data points
     * @return an array containing the labels of each requested row
     */
    default Label[] getLabel(Collection<DataPoint> points){
        return points.stream()
                .map(this::getLabel)
                .toArray(Label[]::new);
    }
}
