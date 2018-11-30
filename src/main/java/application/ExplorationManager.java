package application;

import config.ExperimentConfiguration;
import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;
import data.PartitionedDataset;
import data.preprocessing.StandardScaler;
import machinelearning.active.Ranker;
import machinelearning.threesetmetric.ExtendedLabel;
import utils.RandomState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is meant to be an API to the user frontend, providing all methods necessary for retrieving points to be
 * labeled by the real user.
 */
public class ExplorationManager {
    /**
     * The dataset partition (labeled and unlabeled points)
     */
    private final PartitionedDataset partitionedDataset;

    private final IndexedDataset rawDataset;

    /**
     * The experiment configuration (active learner, initial sampler, ...)
     */
    private final ExperimentConfiguration configuration;

    /**
     * The current Active Learning model
     */
    private Ranker ranker;

    private boolean isInitialSamplingStep;

    /**
     * @param dataset: collection of unlabeled points
     * @param configuration: experiment configurations
     */
    public ExplorationManager(IndexedDataset dataset, ExperimentConfiguration configuration) {
        this.ranker = null;
        this.isInitialSamplingStep = true;
        this.configuration = configuration;
        this.rawDataset = dataset;

        IndexedDataset scaledDataset = rawDataset.copyWithSameIndexes(StandardScaler.fitAndTransform(rawDataset.getData()));
        this.partitionedDataset = getPartitionedDataset(scaledDataset);

    }

    private PartitionedDataset getPartitionedDataset(IndexedDataset dataPoints) {
        return configuration
                .getTsmConfiguration()
                .getMultiTsmModel()
                .map(x -> new PartitionedDataset(dataPoints, x))
                .orElseGet(() -> new PartitionedDataset(dataPoints));
    }

    /**
     * @return an initial selection of points to be labeled by the user
     */
    public List<DataPoint> runInitialSampling(int sampleSize) {
        return rawDataset.sample(sampleSize).toList();
    }

    public List<DataPoint> getNextPointsToLabel(List<LabeledPoint> labeledPoints){

        this.partitionedDataset.update(labeledPoints);
        if (this.isInitialSamplingStep){

            if (this.hasPositiveAndNegativeExamples()){
                this.isInitialSamplingStep = false;
                return Collections.singletonList(this.runExploreIteration(labeledPoints));
            }
            else{
                // TODO : not resample previously propsed points.
                return this.runInitialSampling(3);
            }
        }
        else{
            return Collections.singletonList(this.runExploreIteration(labeledPoints));
        }
    }

    protected boolean hasPositiveAndNegativeExamples(){

        boolean hasPositive = false;
        boolean hasNegative = false;

        for (LabeledPoint point: partitionedDataset.getLabeledPoints()
             ) {


            if (point.getLabel().isPositive()){
                hasPositive = true;
            }
            if (point.getLabel().isNegative()){
                hasNegative = true;
            }
        }

        return hasPositive && hasNegative;
    }

    /**
     * @param labeledPoints: collection of points labeled by the user in the previous iteration
     * @return the next point to be labeled by the user
     */
    public DataPoint runExploreIteration(List<LabeledPoint> labeledPoints) {
        // pick scaled data
        List<LabeledPoint> scaledLabeledPoints = new ArrayList<>(labeledPoints.size());
        for (LabeledPoint point : labeledPoints) {
            long id = point.getId();
            scaledLabeledPoints.add(new LabeledPoint(partitionedDataset.getAllPoints().getFromIndex(id), point.getLabel()));
        }

        DataPoint mostInformativePoint = updateModelAndRetrieveNextPointToLabel(scaledLabeledPoints);

        // the next point's label can be inferred by TSM; in this case, keep selecting points until an UNKNOWN point
        // appears, or the dataset runs empty
        ExtendedLabel extendedLabel = partitionedDataset.getLabel(mostInformativePoint);

        while (extendedLabel.isKnown() && partitionedDataset.hasUnknownPoints()) {
            scaledLabeledPoints = Collections.singletonList(new LabeledPoint(mostInformativePoint, extendedLabel.toLabel()));
            mostInformativePoint = updateModelAndRetrieveNextPointToLabel(scaledLabeledPoints);
            extendedLabel = partitionedDataset.getLabel(mostInformativePoint);
        }

        // get unscaled labeled point
        return rawDataset.getFromIndex(mostInformativePoint.getId());
    }

    private DataPoint updateModelAndRetrieveNextPointToLabel(List<LabeledPoint> labeledPoints) {
        // update data partition
        //partitionedDataset.update(labeledPoints);

        // fit active learning model
        ranker = configuration.getActiveLearner().fit(partitionedDataset.getLabeledPoints());

        // select new point to be labeled
        IndexedDataset unlabeledData = RandomState.newInstance().nextDouble() <= configuration.getTsmConfiguration().getSearchUnknownRegionProbability() ? partitionedDataset.getUnknownPoints() : partitionedDataset.getUnlabeledPoints();
        IndexedDataset sample = unlabeledData.sample(configuration.getSubsampleSize());
        return ranker.top(sample);
    }
}
