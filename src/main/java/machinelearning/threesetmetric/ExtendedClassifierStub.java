package machinelearning.threesetmetric;

import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;

import java.util.Collection;

/**
 * A stub for a {@link ExtendedClassifier}.
 */
public final class ExtendedClassifierStub implements ExtendedClassifier {

    /**
     * @return an empty array
     */
    @Override
    public ExtendedLabel[] predict(IndexedDataset points) {
        return new ExtendedLabel[0];
    }

    /**
     * Nothing is done
     */
    @Override
    public void update(Collection<LabeledPoint> labeledPoints) {
        // do nothing
    }

    /**
     * @return {@link ExtendedLabel#UNKNOWN}
     */
    @Override
    public ExtendedLabel predict(DataPoint dataPoint) {
        return ExtendedLabel.UNKNOWN;
    }

    /**
     * @return false, since no data model is ever built
     */

    @Override
    public boolean isRunning(){
        return false;
    }


    /**
     * @return false, since no labeling is ever done
     */
    public boolean triggerRelabeling(){
        return false;
    }

}
