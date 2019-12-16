/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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

    private TsmConfiguration() {
        // avoid instantiation
    }

    public TsmConfiguration(boolean hasTSM) {
        this.hasTsm = hasTSM;
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

    public void setSearchUnknownRegionProbability(double searchUnknownRegionProbability) {
        this.searchUnknownRegionProbability = searchUnknownRegionProbability;
    }

    public void setFlags(List<boolean[]> flags) {
        this.flags = flags;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
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

    public int[][] getColumnPartitionIndexes() {
        int[][] partition = new int[featureGroups.size()][];

        for (int i = 0; i < partition.length; i++) {
            partition[i] = getColumnIndex(featureGroups.get(i));
        }
        return partition;
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
