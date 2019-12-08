package explore;

import explore.sampling.InitialSampler;
import machinelearning.active.ActiveLearner;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.TSM.MultiTSMLearner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ExperimentConfiguration {
    private String task;
    private ActiveLearner activeLearner;

    private InitialSampler initialSampler;
    private int subsampleSize = Integer.MAX_VALUE;

    private TsmConfiguration multiTSM = new TsmConfiguration(false);

    private boolean useFactorizationInformation = false;

    public String getTask() {
        return task;
    }

    public InitialSampler getInitialSampler() {
        return initialSampler;
    }

    public void setInitialSampler(InitialSampler initialSampler) {
        this.initialSampler = initialSampler;
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

    public TsmConfiguration getTsmConfiguration() {
        return multiTSM;
    }

    public boolean hasFactorizationInformation() {
        return useFactorizationInformation;
    }

    public boolean useFakePoint;

    public boolean getUseFakePoint(){
        return this.useFakePoint;
    }

    private ExperimentConfiguration() {
        // avoid instantiating this class
    }

    public static class TsmConfiguration {

        //private boolean hasTsm = true;
        private boolean hasTsm;
        private double searchUnknownRegionProbability = 0D;
        private List<boolean[]> flags = new ArrayList<>();
        private List<String[]> featureGroups = new ArrayList<>();
        private String[] columns = new String[0];

        public TsmConfiguration() {
            //this(true);
        }

        public TsmConfiguration(boolean hasTsm) {
            this.hasTsm = hasTsm;
        }

        /**
         * @return the probability of searching the Unknown region (as specified in run time). If TSM is not being used, return 0.
         */
        public double getSearchUnknownRegionProbability() {
            return hasTsm ? searchUnknownRegionProbability : 0D;
        }

        public List<boolean[]> getFlags() {
            return flags;
        }

        public void setFlags(List<boolean[]> flags) {
            this.flags = flags;
        }

        public List<String[]> getFeatureGroups() {
            return featureGroups;
        }

        public void setFeatureGroups(List<String[]> featureGroups) {
            this.featureGroups = featureGroups;
        }

        public Optional<ExtendedClassifier> getMultiTsmModel() {
            if (!hasTsm) {
                return Optional.empty();
            }

            List<int[]> featureGroupIndexes = featureGroups.stream()
                    .map(this::getColumnIndex)
                    .collect(Collectors.toList());

            return Optional.of(new MultiTSMLearner(featureGroupIndexes, getFlagsCopy()));
        }

        private List<boolean[]> getFlagsCopy() {
            return flags.stream().map(boolean[]::clone).collect(Collectors.toList());
        }

        public int[][] getColumnPartitionIndexes() {
            int[][] partition = new int[featureGroups.size()][];

            for (int i = 0; i < partition.length; i++) {
                partition[i] = getColumnIndex(featureGroups.get(i));
            }
            return partition;
        }

        private int[] getColumnIndex(String[] attributes) {
            return Arrays.stream(attributes).mapToInt(this::getColumnIndex).toArray();
        }

        private int getColumnIndex(String column) {
            int i = Arrays.asList(columns).indexOf(column);
            if (i < 0) {
                throw new IllegalArgumentException("Column " + column + " not in columns list " + Arrays.toString(columns));
            }
            return i;
        }

        public void setColumns(String[] columns) {
            this.columns = columns;
        }

        @Override
        public String toString() {
            return "TsmConfiguration{" +
                    "hasTSM=" + hasTsm +
                    ", searchUnknownRegionProbability=" + searchUnknownRegionProbability +
                    ", flags=" + flags.stream().map(Arrays::toString).reduce("", (x, y) -> x+", "+y).substring(2) +
                    ", featureGroups=" + featureGroups.stream().map(Arrays::toString).reduce("", (x, y) -> x+", "+y).substring(2) +
                    '}';
        }
    }
}
