package config;

import explore.sampling.InitialSampler;
import explore.sampling.StratifiedSampler;
import machinelearning.active.ActiveLearner;

public final class ExperimentConfiguration {
    private String task;
    private ActiveLearner activeLearner;

    private InitialSampler initialSampler = new StratifiedSampler(1, 1);
    private int subsampleSize = Integer.MAX_VALUE;

    private TsmConfiguration multiTSM;

    public ExperimentConfiguration() {
        // avoid instantiating this class
    }

    public String getTask() {
        return task;
    }

    public ActiveLearner getActiveLearner() {
        return activeLearner;
    }

    public InitialSampler getInitialSampler() {
        return initialSampler;
    }

    public int getSubsampleSize() {
        return subsampleSize;
    }

    public TsmConfiguration getTsmConfiguration() {
        return multiTSM;
    }

}
