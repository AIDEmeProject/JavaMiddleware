package application;

import data.*;

import explore.ExperimentConfiguration;
import explore.ExploreIteration;
import explore.InitialIteration;
import explore.Iteration;

import explore.user.GuiUser;
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

        this.initialSampler = new RandomSampler();

        this.exploreIteration = new ExploreIteration(configuration);

        this.partitionedDataset = new PartitionedDataset(dataset);

        this.ranker = null;

        this.isFirstStep = true;

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
            //return first Points

            return this.initialSampler.getPoints();

        }
        if (this.isFirstStep){

            //HERE I HAVE THE FIVE FIRST LABEL WITH ONE YES and ONE NO

            for (LabeledPoint point: userLabeledPoints){
                this.partitionedDataset.update(point);
            }

            this.ranker = this.configuration.getActiveLearner().fit(this.partitionedDataset.getLabeledPoints());
            this.isFirstStep = false;

        }
        else{

            for (LabeledPoint point: userLabeledPoints){

                GuiUser user = new GuiUser();
                user.setLabel(point);

                result = this.exploreIteration.run(this.partitionedDataset, user, ranker);

                this.ranker = result.getRanker();
            }
            GuiUser user = new GuiUser();
            return (ArrayList<DataPoint>) this.exploreIteration.getNextPointsToLabel(this.partitionedDataset, user, this.ranker);
        }
    }
}
