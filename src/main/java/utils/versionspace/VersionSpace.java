package utils.versionspace;

import classifier.Classifier;
import data.LabeledPoint;

import java.util.ArrayList;
import java.util.Collection;

public abstract class VersionSpace {
    protected Collection<LabeledPoint> labeledPoints;

    public VersionSpace() {
        labeledPoints = new ArrayList<>();
    }

    public int getDim(){
        return labeledPoints.isEmpty() ? 0 : labeledPoints.iterator().next().getDim();
    }

    public void setLabeledPoints(Collection<LabeledPoint> labeledPoints) {
        //TODO: copy collection ?
        this.labeledPoints = labeledPoints;
    }

    abstract public Classifier[] sample(int numSamples);
}
