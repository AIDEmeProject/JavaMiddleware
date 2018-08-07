package utils.versionspace;

import classifier.Classifier;
import data.LabeledPoint;

import java.util.Collection;

public interface VersionSpace {
    Classifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples);
}
