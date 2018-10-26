package data;

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
    private IndexedDataset dataset;

    /**
     * Label of each data point
     */
    private Label[] labels;

    /**
     * @param indexes: indexes of each data point
     * @param data: feature matrix (each row represents a data point)
     * @param labels: the label of each data point
     * @throws IllegalArgumentException if inputs do not have the same size or are empty
     */
    public LabeledDataset(List<Long> indexes, Matrix data, Label[] labels) {
        this(new IndexedDataset(indexes, data), labels);
    }

    /**
     * @param dataset: a collection of data points and indexes
     * @param labels: the labels for each data point
     * @throws IllegalArgumentException if the dataset and the labels have incompatible sizes
     */
    public LabeledDataset(IndexedDataset dataset, Label[] labels) {
        Validator.assertEquals(dataset.length(), labels.length);
        this.dataset = dataset;
        this.labels = labels;
    }

    public List<Long> getIndexes() {
        return dataset.getIndexes();
    }

    public Matrix getData() {
        return dataset.getData();
    }

    public Label[] getLabels() {
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
     * @return a new LabeledDataset object with same indexes and labells as {@code this}, but with the underlying data
     * matrix replaced by the input one
     */
    public LabeledDataset copyWithSameIndexesAndLabels(Matrix data) {
        return new LabeledDataset(dataset.getIndexes(), data, labels);
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
}
