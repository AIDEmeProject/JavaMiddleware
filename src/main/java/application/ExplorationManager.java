package application;

import application.trace.PredictionReader;
import config.ExperimentConfiguration;
import data.*;
import data.preprocessing.StandardScaler;
import explore.metrics.ConfusionMatrix;
import explore.metrics.ConfusionMatrixCalculator;
import explore.metrics.MetricStorage;
import explore.metrics.ThreeSetMetricCalculator;
import explore.statistics.Statistics;
import explore.user.GuiUserLabel;
import explore.user.UserLabel;
import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import machinelearning.threesetmetric.ExtendedLabel;
import utils.RandomState;
import utils.linalg.Matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is meant to be an API to the user frontend, providing all methods necessary for retrieving points to be
 * labeled by the real user.
 */
public class ExplorationManager {

    /**
     * The dataset partition (labeled and unlabeled points)
     * Scaled dataset
     */
    private final PartitionedDataset partitionedDataset;

    private final IndexedDataset rawDataset;

    /**
     * this dataset is used to plot model decision boundary
     */
    private IndexedDataset gridOfFakePoints;

    /**
     * Scaled version of fake point grid
     */
    private IndexedDataset scaledGridOfFakePoints;


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


    /**
     * @param dataset: collection of unlabeled points
     * @param configuration: experiment configurations
     */
    public ExplorationManager(IndexedDataset dataset, ExperimentConfiguration configuration, Learner learner) {

        this.ranker = null;
        this.isInitialSamplingStep = true;
        this.configuration = configuration;
        this.learner = learner;


        this.rawDataset = dataset;
        IndexedDataset scaledDataset = this.scaleDataset(rawDataset);

        if (configuration.hasFactorizationInformation()){
            scaledDataset.setFactorizationStructure(configuration.getTsmConfiguration().getColumnPartitionIndexes());
        }

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


    public void addLabeledPointToDataset(LabeledPoint point){
        this.partitionedDataset.addLabeledPointToDataset(point);
    }

    public List<DataPoint> getNextPointsToLabel(List<LabeledPoint> labeledPoints){


        // pick scaled data
        List<LabeledPoint> scaledLabeledPoints = new ArrayList<>(labeledPoints.size());

        for (LabeledPoint point : labeledPoints) {
            long rowId = point.getId(); // No +1 or -1


            LabeledPoint lblPoint = new LabeledPoint(partitionedDataset.getAllPoints().getFromIndex(rowId), point.getLabel());
            /*
            System.out.println("---datapoint---");
            System.out.println(lblPoint.getLabel());
            System.out.println(lblPoint.getData());
            System.out.println("");
            */
            //LabeledPoint rawlblPoint = new LabeledPoint(rawDataset.getFromIndex(rowId), point.getLabel());

            System.out.println("--row ids --");
            System.out.println(rowId);

            /*
            System.out.println("--raw datapoint --");
            System.out.println(rawlblPoint.getId());
            System.out.println(rawlblPoint.getLabel());
            System.out.println(rawlblPoint.getData());
            System.out.println("");
            */

            System.out.println("--scaled dataset --");
            System.out.println(lblPoint.getId());
            System.out.println(lblPoint.getLabel());
            System.out.println(lblPoint.getData());

            System.out.println("");

            scaledLabeledPoints.add(lblPoint);
        }

        this.partitionedDataset.update(scaledLabeledPoints);
        System.out.println("--IS INITIAL SAMPLING--");
        System.out.print(this.isInitialSamplingStep);
        System.out.println("");
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
        boolean useUnknown = partitionedDataset.hasUnknownPoints() && RandomState.newInstance().nextDouble() <= configuration.getTsmConfiguration().getSearchUnknownRegionProbability();
        IndexedDataset unlabeledData = useUnknown ? partitionedDataset.getUnknownPoints() : partitionedDataset.getUnlabeledPoints();
        IndexedDataset sample = unlabeledData.sample(configuration.getSubsampleSize());
        return ranker.top(sample);
    }


    public List<DataPoint> getPointsFromFilters(Collection<Long> indexes){
        return rawDataset.getFromSecondaryIndex(indexes);
    }

    public void printF1Score(){

        UserLabel[] trueLabels = partitionedDataset.getLabeledPoints().getLabels();
        ArrayList<LabeledPoint> predictedLabels = this.labelPoints(partitionedDataset.getLabeledPoints().getDataset(), rawDataset, false);

        UserLabel[] userPredictedLabels = new UserLabel[trueLabels.length];

        for (int i= 0; i<trueLabels.length; i++){

            LabeledPoint point = predictedLabels.get(i);
            UserLabel label = new GuiUserLabel(point.getLabel().asSign());
            userPredictedLabels[i] = label;
        }
        ConfusionMatrix matrix = ConfusionMatrixCalculator.compute(trueLabels, userPredictedLabels);

        System.out.println("---f1 score---");
        System.out.println(matrix.fscore());
    }


    protected IndexedDataset scaleDataset(IndexedDataset dataset){
        this.scaler = StandardScaler.fit(dataset.getData());
        return scaler.transform(dataset);
    }

    public ArrayList<LabeledPoint> labelPoints(IndexedDataset pointsToLabel, IndexedDataset rawPoints, boolean scaleDataset){


        IndexedDataset datasetToLabel;
        LabeledDataset labeledDatasetDbd = this.partitionedDataset.getLabeledPoints();

        Classifier classifier = this.learner.fit(labeledDatasetDbd);

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

            DataPoint rawPoint = rawPoints.get(i);

            long pointId = point.getId();


            Label label = classifier.predict(point.getData());
            LabeledPoint labeledPoint = new LabeledPoint(rawPoint, label);

            labeledDataset.add(labeledPoint);
        }

        return labeledDataset;
    }


    public ArrayList<LabeledPoint> labelWholeDataset(){
        //add user labeled points
        IndexedDataset pointsToLabel = this.partitionedDataset.getAllPoints();
        return this.labelPoints(pointsToLabel, rawDataset,  false);
    }

    protected void generateGridOfFakePoints(){


        Statistics[] columnStatistics = this.rawDataset.getData().columnStatistics();
        ArrayList<ColumnSpecification> specs = new ArrayList<>();

        for (int i = 0; i < columnStatistics.length; i++) {
            double min = columnStatistics[i].getMinimum();
            double max = columnStatistics[i].getMaximum();

            boolean isNumeric = columnStatistics[i].isNumeric();
            specs.add(new ColumnSpecification(isNumeric, min, max, 50));

        }

        GridPointGenerator generator = new GridPointGenerator(specs);

        this.gridOfFakePoints = generator.generatePoints();

        Matrix scaledFakePoints = this.scaler.transform(this.gridOfFakePoints.getData());
        this.scaledGridOfFakePoints = this.gridOfFakePoints.copyWithSameIndexes(scaledFakePoints);
        System.out.println("scaled fake point grid number and unscaled");
        System.out.println(this.scaledGridOfFakePoints.length());
        System.out.println(this.gridOfFakePoints.length());
    }

    protected IndexedDataset getScaledGridOfFakePoints(){
        if (this.scaledGridOfFakePoints == null){
            this.generateGridOfFakePoints();
        }

        return this.scaledGridOfFakePoints;
    }

    public IndexedDataset getGridOfFakePoints(){
        if (this.gridOfFakePoints == null){
            this.generateGridOfFakePoints();
        }
        return this.gridOfFakePoints;
    }

    public IndexedDataset getRawDataset(){
        return this.partitionedDataset.getAllPoints();
    }


    public ArrayList<LabeledPoint> computeTSMPredictionsOverFakeGridPoints(){
        System.out.println("GRID POINT ?");
        System.out.println(this.getScaledGridOfFakePoints().length());
        System.out.println("-------");
        return this.TSMPrediction(this.getScaledGridOfFakePoints());
    }


    public ArrayList<LabeledPoint> computeModelPredictionsOverFakeGridPoints(){

        return this.labelPoints(this.getScaledGridOfFakePoints(), this.getGridOfFakePoints(), false);
    }


    public ArrayList<LabeledPoint> computeModelPredictionsOverRealDataset(){

        if (! this.configuration.hasMultiTSM()){
            return this.labelPoints(partitionedDataset.getAllPoints(), rawDataset, false);
        }

        IndexedDataset datasetToLabel = this.partitionedDataset.getAllPoints();
        return this.getModelPredictionWithTSM(datasetToLabel);
    }

    public List<LabeledPoint> getLabeledPointFromIteration(PredictionReader reader, int iteration) {
        return reader.getPredictionsForIteration(rawDataset, iteration);
    }


    public ArrayList<LabeledPoint> computeLabelOfFakeGridPoint(){

        if (! this.configuration.hasMultiTSM()){

            return this.labelPoints(this.getScaledGridOfFakePoints(), this.getGridOfFakePoints(), false);

        }

        return this.TSMPrediction(this.getScaledGridOfFakePoints());
    }

    public ArrayList<LabeledPoint> computeModelPredictionForProjection(){

        if (! this.configuration.hasMultiTSM()){
            return this.labelPoints(partitionedDataset.getAllPoints().sample(2), rawDataset.sample(2), false);
        }

        IndexedDataset datasetToLabel = this.partitionedDataset.getAllPoints();
        System.out.println("TOBE REMOVED AFTER DBG");
        return this.TSMPrediction(datasetToLabel.sample(2));
    }



    public ArrayList<LabeledPoint> getModelPredictionWithTSMOnRealData(){
        return this.getModelPredictionWithTSM(this.partitionedDataset.getAllPoints());
    }

    public ArrayList<LabeledPoint> computeTSMPredictionOverRealDataset(){
        return this.TSMPrediction(partitionedDataset.getAllPoints());
    }


    public ArrayList<LabeledPoint> getTSMPredictionOnRealData(){

        //return this.TSMPrediction(this.checkPositivePointPrediction());
        return this.TSMPrediction(this.partitionedDataset.getAllPoints());
    }

    public ArrayList<LabeledPoint> TSMPrediction(IndexedDataset datasetToLabel){

        ExtendedLabel[] labels = this.partitionedDataset.getTSMClassifier().predict(datasetToLabel);

        ArrayList<LabeledPoint> labeledDataset = new ArrayList<>();

        System.out.println("-----------------TSM----------------");
        System.out.println(new ThreeSetMetricCalculator().compute(this.partitionedDataset, null).getMetrics());
        System.out.println();

        for (int i=0; i < labels.length; i++)
        {
            ExtendedLabel label = labels[i];

            DataPoint rawPoint = datasetToLabel.get(i);

            GuiUserLabel guiLabel = GuiUserLabel.fromExtendedLabel(label);
            LabeledPoint labeledPoint = new LabeledPoint(rawPoint, guiLabel);

            labeledDataset.add(labeledPoint);
        }

        return labeledDataset;
    }

    public IndexedDataset checkPositivePointPrediction(){


        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0, this.partitionedDataset.get(2554).getData().toArray());
        builder.add(1, this.partitionedDataset.get(2555).getData().toArray());
        builder.add(2, this.partitionedDataset.get(2556).getData().toArray());

        builder.add(3, this.partitionedDataset.get(1457).getData().toArray());
        builder.add(4, this.partitionedDataset.get(1458).getData().toArray());
        builder.add(5, this.partitionedDataset.get(1459).getData().toArray());


        builder.add(6, this.partitionedDataset.get(2561).getData().toArray());
        builder.add(7, this.partitionedDataset.get(2562).getData().toArray());
        builder.add(8, this.partitionedDataset.get(2563).getData().toArray());

        builder.add(9, this.partitionedDataset.get(1454).getData().toArray());
        builder.add(10, this.partitionedDataset.get(1455).getData().toArray());
        builder.add(11, this.partitionedDataset.get(1456).getData().toArray());


        IndexedDataset positivePoints = builder.build();

        System.out.println(positivePoints.get(10));
        return positivePoints;

    }


    public ArrayList<LabeledPoint> getModelPredictionWithTSM(IndexedDataset datasetToLabel){

        ArrayList<LabeledPoint> labeledDataset = new ArrayList<>();
        ExtendedLabel[] labels = this.partitionedDataset.getTSMClassifier().predict(datasetToLabel);
        Classifier standardClassifier = this.learner.fit(partitionedDataset.getLabeledPoints());

        Label finalLabel;
        for (int i=0; i < labels.length; i++)
        {
            ExtendedLabel label = labels[i];

            DataPoint rawPoint = datasetToLabel.get(i);

            if (label.isUnknown()){
                finalLabel = standardClassifier.predict(rawPoint.getData());
            }
            else{
                finalLabel = label.toLabel();
            }

            LabeledPoint labeledPoint = new LabeledPoint(rawPoint, finalLabel);

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

    public boolean useFactorizationInformation(){
        return configuration.hasFactorizationInformation();
    }

    public boolean useTSM(){
        return configuration.hasMultiTSM();
    }

    public ArrayList<DataPoint> getPointByRowId(int id){
        DataPoint point = this.rawDataset.get(id);

        ArrayList<DataPoint> points = new ArrayList<>();
        points.add(point);
        return points;
    }


    public DataPoint getPoint(long index){
        return this.partitionedDataset.getAllPoints().get((int) index);
    }


}


class ModelPerformanceData{


    public double TSMbound;

    public double sdsd;
}