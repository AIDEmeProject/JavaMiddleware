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

package data;

import explore.user.UserLabel;
import machinelearning.classifier.Label;
import utils.Validator;
import utils.linalg.Matrix;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This module is a in-memory storage for labeled data points. Its main functionality is to provide easy access to its
 * data under several different formats (iterator, stream, range, ...).
 *
 * @see DataPoint
 */
public class LabeledDataset implements Iterable<LabeledPoint> {
    /**
     * Data points and their indexes
     */
    private final IndexedDataset dataset;

    /**
     * Label of each data point
     */
    private final UserLabel[] labels;

    private Label[][] partialLabels;

    /**
     * @param indexes: indexes of each data point
     * @param data: feature matrix (each row represents a data point)
     * @param labels: the label of each data point
     * @throws IllegalArgumentException if inputs do not have the same size or are empty
     */
    public LabeledDataset(List<Long> indexes, Matrix data, UserLabel[] labels) {
        this(new IndexedDataset(indexes, data), labels);
    }

    /**
     * @param dataset: a collection of data points and indexes
     * @param labels: the labels for each data point
     * @throws IllegalArgumentException if the dataset and the labels have incompatible sizes
     */
    public LabeledDataset(IndexedDataset dataset, UserLabel[] labels) {
        Validator.assertEquals(dataset.length(), labels.length);
        this.dataset = dataset;
        this.labels = labels;

        if (dataset.hasFactorizationStructure()) {
            Validator.assertEquals(dataset.partitionSize(), labels[0].getLabelsForEachSubspace().length);

            this.partialLabels = new Label[dataset.partitionSize()][labels.length];
            for (int i = 0; i < partialLabels.length; i++) {
                for (int j = 0; j < labels.length; j++) {
                    this.partialLabels[i][j] = labels[j].getLabelsForEachSubspace()[i];
                }
            }
        }

    }

    public List<Long> getIndexes() {
        return dataset.getIndexes();
    }

    public Matrix getData() {
        return dataset.getData();
    }

    public UserLabel getLabel(int index) {
        return labels[index];
    }

    public UserLabel[] getLabels() {
        return labels;
    }

    /**
     * @return number of data points
     */
    public int length() {
        return labels.length;
    }

    /**
     * @return dimension of each data point
     */
    public int dim() {
        return dataset.dim();
    }

    /**
     * @param i: row index of labeled point to retrieve
     * @return the labeled point at row {@code i}.
     * @throws IndexOutOfBoundsException if {@code i} is out-of-bounds
     */
    public LabeledPoint get(int i) {
        return new LabeledPoint(dataset.get(i), labels[i]);
    }

    /**
     * @param data: new features matrix
     * @return a new LabeledDataset object with same indexes and labels as {@code this}, but with the underlying data
     * matrix replaced by the input one
     */
    public LabeledDataset copyWithSameIndexesAndLabels(Matrix data) {
        return new LabeledDataset(dataset.getIndexes(), data, labels);
    }

    public LabeledDataset append(IndexedDataset data, UserLabel[] label) {
        Validator.assertEquals(data.length(), label.length);
        Validator.assertEquals(data.dim(), dim());

        UserLabel[] stackedLabels = new Label[labels.length + label.length];
        System.arraycopy(labels, 0, stackedLabels, 0, labels.length);
        System.arraycopy(label, 0, stackedLabels, labels.length, label.length);
        return new LabeledDataset(dataset.append(data), stackedLabels);
    }

    @Override
    public Iterator<LabeledPoint> iterator() {
        return new Iterator<LabeledPoint>() {
            int row = 0;

            @Override
            public boolean hasNext() {
                return row < labels.length;
            }

            @Override
            public LabeledPoint next() {
                return get(row++);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabeledDataset that = (LabeledDataset) o;
        return Objects.equals(dataset, that.dataset) &&
                Arrays.equals(labels, that.labels);
    }

    public LabeledDataset[] getPartitionedData() {
        if (!dataset.hasFactorizationStructure()) {
            return new LabeledDataset[]{this}; //new LabeledDataset(dataset, labels)
        }

        IndexedDataset[] partitionedDatasets = dataset.getPartitionedData();

        LabeledDataset[] labeledDatasets = new LabeledDataset[dataset.partitionSize()];
        for (int i = 0; i < labeledDatasets.length; i++) {
            labeledDatasets[i] = new LabeledDataset(partitionedDatasets[i], partialLabels[i]);
        }

        return labeledDatasets;
    }

    public int partitionSize() {
        return dataset.partitionSize();
    }

    public int[][] getPartitionIndexes() {
        return dataset.getPartitionIndexes();
    }

    public IndexedDataset getDataset() {
        return dataset;
    }
}
