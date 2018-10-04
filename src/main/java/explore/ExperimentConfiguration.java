package explore;

import explore.sampling.InitialSampler;
import explore.sampling.StratifiedSampler;
import machinelearning.active.ActiveLearner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ExperimentConfiguration {
    private String task;
    private ActiveLearner activeLearner;

    private InitialSampler initialSampler = new StratifiedSampler(1, 1);
    private int subsampleSize = Integer.MAX_VALUE;
    private TsmConfiguration multiTSM = new TsmConfiguration(false);

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

    public boolean hasMultiTSM() {
        return multiTSM.hasTsm;
    }

    public double getSearchUncertainRegionProbability() {
        return multiTSM.searchUnknownRegionProbability;
    }

    private ExperimentConfiguration() {
        // avoid instantiating this class
    }

    private static class TsmConfiguration {
        private boolean hasTsm = true;
        private double searchUnknownRegionProbability = 0D;
        private List<boolean[]> flags = new ArrayList<>();
        private List<String[]> featureGroups = new ArrayList<>();

        public TsmConfiguration() {
            this(true);
        }

        public TsmConfiguration(boolean hasTsm) {
            this.hasTsm = hasTsm;
        }
    }
}
