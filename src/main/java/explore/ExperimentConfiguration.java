package explore;

import explore.sampling.InitialSampler;
import explore.sampling.StratifiedSampler;
import machinelearning.active.ActiveLearner;

public final class ExperimentConfiguration {
    private String task;
    private ActiveLearner activeLearner;

    private InitialSampler initialSampler = new StratifiedSampler(1, 1);
    private int subsampleSize = Integer.MAX_VALUE;
    private double searchUncertainRegionProbability = 0;

    // TODO: add TSM model here

    public String getTask() {
        return task;
    }

    public InitialSampler getInitialSampler() {
        return initialSampler;
    }

    public int getSubsampleSize() {
        return subsampleSize;
    }

    public ActiveLearner getActiveLearner() {
        return activeLearner;
    }

    public double getSearchUncertainRegionProbability() {
        return searchUncertainRegionProbability;
    }

    private ExperimentConfiguration() {
        // avoid instantiating this class
    }
}
