package config;

import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.TSM.MultiTSMLearner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TsmConfiguration {
    private boolean hasTsm = true;
    private double searchUnknownRegionProbability = 0D;
    private List<boolean[]> flags = new ArrayList<>();
    private List<String[]> featureGroups = new ArrayList<>();
    private String[] columns = new String[0];

    public TsmConfiguration(boolean hasTsm) {
        this.hasTsm = hasTsm;
    }

    public boolean hasTsm() {
        return hasTsm;
    }

    public double getSearchUnknownRegionProbability() {
        return hasTsm ? searchUnknownRegionProbability : 0D;
    }

    public List<String[]> getFeatureGroups() {
        return featureGroups;
    }

    public void setFeatureGroups(List<String[]> featureGroups) {
        this.featureGroups = featureGroups;
    }

    public void setFlags(List<boolean[]> flags) {
        this.flags = flags;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public boolean hasEmptyFactorizationStructure() {
        return flags.isEmpty();
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


    private int[] getColumnIndex(String[] attributes) {
        return Arrays.stream(attributes).mapToInt(this::getColumnIndex).toArray();
    }

    private int getColumnIndex(String column) {
        int i = 0;
        for (String col : columns) {
            if (column.equals(col)) {
                return i;
            }
            i++;
        }
        throw new IllegalArgumentException("Column " + column + " not in columns list " + Arrays.toString(columns));
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
