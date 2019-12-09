package config;

import explore.sampling.InitialSampler;
import explore.sampling.StratifiedSampler;
import machinelearning.active.ActiveLearner;

public final class ExperimentConfiguration {

    private String task;
    private boolean useFakePoint;
    private ActiveLearner activeLearner;
    private int subsampleSize = Integer.MAX_VALUE;
    private boolean useFactorizationInformation = false;
    private TsmConfiguration multiTSM = new TsmConfiguration(false);
    private InitialSampler initialSampler = new StratifiedSampler(1, 1);

    private ExperimentConfiguration() {
        // avoid instantiating this class
    }

    public String getTask() {
        return task;
    }

    public boolean getUseFakePoint(){
        return this.useFakePoint;
    }

    public ActiveLearner getActiveLearner() {
        return activeLearner;
    }

    public int getSubsampleSize() {
        return subsampleSize;
    }

    public boolean hasFactorizationInformation() {
        return useFactorizationInformation;
    }

    public boolean hasMultiTSM() {
        return multiTSM.hasTsm();
    }

    public TsmConfiguration getTsmConfiguration() {
        return multiTSM;
    }

    public InitialSampler getInitialSampler() {
        return initialSampler;
    }

    public void setInitialSampler(InitialSampler initialSampler) {
        this.initialSampler = initialSampler;
    }
}
