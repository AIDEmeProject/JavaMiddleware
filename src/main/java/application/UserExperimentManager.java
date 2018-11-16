package application;

import data.*;

import config.ExperimentConfiguration;
import explore.ExploreIteration;
import explore.InitialIteration;
import explore.Iteration;


import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;

import java.util.ArrayList;

public class UserExperimentManager implements FacadeInterface {

    private final ExperimentConfiguration configuration;

    private final ExploreIteration exploreIteration;

    private final RandomSampler initialSampler;

    private final PartitionedDataset partitionedDataset;

    private final InitialIteration intialIteration;

    private Ranker ranker;

    private boolean isFirstStep;

    /**
     * @param folder: the exploration folder where results will be stored
     * @param dataPoints: unlabeled pool of points
     * @param user: the user for labeling points
     */
    public UserExperimentManager(ExperimentConfiguration configuration, IndexedDataset dataset) {

        this.configuration = configuration;

        this.intialIteration = new InitialIteration(configuration);

        this.exploreIteration = new ExploreIteration(configuration);

        this.partitionedDataset = new PartitionedDataset(dataset);

        this.ranker = null;

        this.isFirstStep = true;

        this.initialSampler = new RandomSampler(this.partitionedDataset);
    }

    public LabeledDataset getLabeledDataset(Classifier classifier){

        IndexedDataset unlabeledPoints = this.partitionedDataset.getUnlabeledPoints();

        LabeledDataset labeledDataset = new LabeledDataset(unlabeledPoints, classifier.predict(unlabeledPoints));

        return labeledDataset;

    }

    @Override
    public ArrayList<DataPoint> nextIteration(ArrayList<LabeledPoint> userLabeledPoints) {

        Iteration.Result result;
        if (userLabeledPoints.isEmpty()){
            return this.initialSampler.getPoints(3);
        }
        if (this.isFirstStep){

            //HERE I HAVE THE FIVE FIRST LABEL WITH ONE YES and ONE NO
            //this.intialIteration.run(this.partitionedDataset);
            for (LabeledPoint point: userLabeledPoints){
                this.partitionedDataset.update(point);
            }

            this.ranker = this.configuration.getActiveLearner().fit(this.partitionedDataset.getLabeledPoints());
            this.isFirstStep = false;
            return new ArrayList<DataPoint>();
        }
        else{
/*
            for (LabeledPoint point: userLabeledPoints){

                GuiUser user = new GuiUser();
                user.setLabel(point);

                result = this.exploreIteration.run(this.partitionedDataset, user, ranker);

                this.ranker = result.getRanker();
            }
            GuiUser user = new GuiUser();
            return (ArrayList<DataPoint>) this.exploreIteration.getNextPointsToLabel(this.partitionedDataset, user, this.ranker);
            */
            return new ArrayList<DataPoint>();
        }
    }
}
