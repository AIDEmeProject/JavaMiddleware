package active.learning;

import classifier.linear.VersionSpaceSamplingClassifier;
import utils.versionspace.VersionSpace;

public class GeneralizedBinarySearch extends UncertaintySampler {
    public GeneralizedBinarySearch(VersionSpace versionSpace, int numSamples) {
        super(new VersionSpaceSamplingClassifier(versionSpace, numSamples));
    }
}
