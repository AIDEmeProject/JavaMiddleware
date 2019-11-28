package application;

import data.*;

import explore.ExperimentConfiguration;
import explore.metrics.MetricStorage;
import explore.metrics.ThreeSetMetricCalculator;

import data.preprocessing.StandardScaler;

import explore.user.GuiUserLabel;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;

import machinelearning.threesetmetric.ExtendedLabel;
import utils.RandomState;
import utils.linalg.Matrix;


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

    private IndexedDataset encodedDataset;

    /**
     * The experiment configuration (active learner, initial sampler, ...)
     */
    private final ExperimentConfiguration configuration;

    private final Learner learner;

    /**
     * The current Active Learning model
     */
    private Ranker ranker;

    private StandardScaler scaler;

    private boolean isInitialSamplingStep;

    private IndexedDataset gridOfFakePoints;

    /**
     * @param dataset: collection of unlabeled points
     * @param configuration: experiment configurations
     */
    public ExplorationManager(IndexedDataset dataset, ExperimentConfiguration configuration, Learner learner) {
        this.ranker = null;
        this.isInitialSamplingStep = true;
        this.configuration = configuration;

        this.rawDataset = dataset;
        this.learner = learner;

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

        if (this.configuration.getUseFakePoint()){
            FakePointInitialSamplingGenerator generator = new FakePointInitialSamplingGenerator();
            return generator.getFakePoint(this.rawDataset);
        }
        return rawDataset.sample(sampleSize).toList();
    }

    public ArrayList<DataPoint> getPointByRowId(int id){
        DataPoint point = this.rawDataset.get(id);

        ArrayList<DataPoint> points = new ArrayList();
        points.add(point);
        return points;
    }


    public void addLabeledPointToDataset(LabeledPoint point){
        this.partitionedDataset.addLabeledPointToDataset(point);
    }

    public List<DataPoint> getNextFakePoint(){


        ArrayList<DataPoint> fakePoints = new ArrayList();
        fakePoints.add(this.rawDataset.getFakeData());
        return fakePoints;
    }

    public List<DataPoint> getNextPointsToLabel(List<LabeledPoint> labeledPoints){


        // pick scaled data
        List<LabeledPoint> scaledLabeledPoints = new ArrayList<>(labeledPoints.size());

        for (LabeledPoint point : labeledPoints) {
            long id = point.getId();
            scaledLabeledPoints.add(new LabeledPoint(partitionedDataset.getAllPoints().getFromIndex(id), point.getLabel()));
        }

        this.partitionedDataset.update(scaledLabeledPoints);
        if (this.isInitialSamplingStep){

            if (this.hasPositiveAndNegativeExamples()){
                this.isInitialSamplingStep = false;
                return Collections.singletonList(this.runExploreIteration(scaledLabeledPoints));
            }
            else{
                // TODO : not resample previously propsed points.
                return this.runInitialSampling(3);
            }
        }
        else{

            return Collections.singletonList(this.runExploreIteration(scaledLabeledPoints));
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

        DataPoint mostInformativePoint = updateModelAndRetrieveNextPointToLabel(labeledPoints);

        // the next point's label can be inferred by TSM; in this case, keep selecting points until an UNKNOWN point
        // appears, or the dataset runs empty
        ExtendedLabel extendedLabel = partitionedDataset.getLabel(mostInformativePoint);

        System.out.println("unkown points");
        System.out.println(partitionedDataset.hasUnknownPoints());
        System.out.println(partitionedDataset.getUnknownPoints().length());

        while (extendedLabel.isKnown() && partitionedDataset.hasUnknownPoints()) {
            labeledPoints = Collections.singletonList(new LabeledPoint(mostInformativePoint, extendedLabel.toLabel()));
            mostInformativePoint = updateModelAndRetrieveNextPointToLabel(labeledPoints);
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


    protected IndexedDataset scaleDataset(IndexedDataset dataset){

        StandardScaler scaler = StandardScaler.fit(dataset.getData());
        this.scaler = scaler;
        IndexedDataset scaledDataset = dataset.copyWithSameIndexes(scaler.transform(dataset.getData()));
        return scaledDataset;
    }

    public ArrayList<LabeledPoint> labelPoints(IndexedDataset pointsToLabel, boolean scaleDataset){


        IndexedDataset datasetToLabel;
        Classifier classifier = this.learner.fit(this.partitionedDataset.getLabeledPoints());

        if (scaleDataset){

            datasetToLabel = this.scaleDataset(pointsToLabel);
        }
        else{

            datasetToLabel = pointsToLabel;
        }

        ArrayList<LabeledPoint> labeledDataset = new ArrayList<>();

        for (int i=0;i < datasetToLabel.length(); i++)
        {
            DataPoint point = datasetToLabel.get(i);
            DataPoint rawPoint = this.rawDataset.get(i);

            Label label = classifier.predict(point.getData());
            LabeledPoint labeledPoint = new LabeledPoint(rawPoint, label);

            labeledDataset.add(labeledPoint);
        }

        return labeledDataset;
    }

    public ArrayList<LabeledPoint> labelWholeDataset(){

        //add user labeled points
        //IndexedDataset pointsToLabel = this.partitionedDataset.getAllPoints();
        IndexedDataset pointsToLabel = this.partitionedDataset.getAllPoints();

        return this.labelPoints(pointsToLabel, false);
    }

    protected IndexedDataset getOrGenerateGridOfFakePoints(){

        if ( this.gridOfFakePoints != null){
            return this.gridOfFakePoints;
        }

        ArrayList<ColumnSpecification> specs = new ArrayList<>();
        specs.add(new ColumnSpecification(true, (float) -0.1964459298, (float) 169.1100834044, 100));
        specs.add(new ColumnSpecification(false, 1, 3, 0));

        //Matrix data = this.partitionedDataset.getAllPoints().getData();

        GridPointGenerator generator = new GridPointGenerator(specs);

        this.gridOfFakePoints = generator.generatePoints();
        this.gridOfFakePoints.copyWithSameIndexes(this.scaler.transform(this.gridOfFakePoints.getData()));

        return this.gridOfFakePoints;
    }

    public ArrayList<LabeledPoint> computeLabelOfFakeGridPoint(){

        if (! this.configuration.hasMultiTSM()){

            return this.labelPoints(this.getOrGenerateGridOfFakePoints(), false);
        }
        System.out.println("TSM PREDICTION");
        return this.TSMPrediction(this.getOrGenerateGridOfFakePoints());
    }

    public ArrayList<LabeledPoint> computeModelPredictionForProjection(){

        if (! this.configuration.hasMultiTSM()){
            return this.labelWholeDataset();
        }

        IndexedDataset datasetToLabel = this.partitionedDataset.getAllPoints();
        return this.TSMPrediction(datasetToLabel);
    }

    public ArrayList<LabeledPoint> TSMPrediction(IndexedDataset datasetToLabel){

        ExtendedLabel[] labels = this.partitionedDataset.getTSMClassifier().predict(datasetToLabel);

        ArrayList<LabeledPoint> labeledDataset = new ArrayList<>();

        for (int i=0; i < labels.length; i++)
        {
            ExtendedLabel label = labels[i];

            System.out.println(label.asSign());
            DataPoint rawPoint = datasetToLabel.get(i);

            GuiUserLabel guiLabel = GuiUserLabel.fromExtendedLabel(label);
            LabeledPoint labeledPoint = new LabeledPoint(rawPoint, guiLabel);

            labeledDataset.add(labeledPoint);
        }

        return labeledDataset;
    }

    public ArrayList<LabeledPoint> labelWholeDataset(int n){

        Classifier classifier = this.learner.fit(this.partitionedDataset.getLabeledPoints());

        ArrayList<LabeledPoint> labeledDataset = new ArrayList<>();

        IndexedDataset unlabeledPoints = this.partitionedDataset.getUnlabeledPoints();
        for ( int i = 0; i < n ; i++)
        {
            DataPoint point = unlabeledPoints.get(i);
            Label label = classifier.predict(point.getData());

            LabeledPoint labeledPoint = new LabeledPoint(point, label);
            labeledDataset.add(labeledPoint);
        }

        //add user labeled points

        return labeledDataset;
    }


    public Double getTSMBound(){

        ThreeSetMetricCalculator calculator = new ThreeSetMetricCalculator();

        MetricStorage storage = calculator.compute(this.partitionedDataset, null);

        Double lowerBound = storage.getMetrics().get("ThreeSetMetric");
        return lowerBound;

    }
}


class ModelPerformanceData{


    public double TSMbound;

    public double sdsd;
}