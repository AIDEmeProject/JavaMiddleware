package explore;

import explore.sampling.InitialSampler;
import explore.sampling.StratifiedSampler;
import machinelearning.active.ActiveLearner;

public final class ExperimentConfiguration {
    private String task;
    private int budget;
    private ActiveLearner activeLearner;

    private InitialSampler initialSampler = new StratifiedSampler(1, 1);
    private int subsampleSize = Integer.MAX_VALUE;

    public String getTask() {
        return task;
    }

    public int getBudget() {
        return budget;
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

    private ExperimentConfiguration() {
        // avoid instantiating this class
    }
}
