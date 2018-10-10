package data;

import machinelearning.classifier.Label;
import utils.Validator;
import utils.linalg.Matrix;

import java.util.Iterator;
import java.util.List;

public class LabeledDataset implements Iterable<LabeledPoint> {
    private IndexedDataset dataset;
    private Label[] labels;

    public final static LabeledDataset EMPTY = new LabeledDataset(IndexedDataset.EMPTY, new Label[0]);

    public LabeledDataset(List<Long> indexes, Matrix data, Label[] labels) {
        this(new IndexedDataset(indexes, data), labels);
    }

    public LabeledDataset(IndexedDataset dataset, Label[] labels) {
        Validator.assertEquals(dataset.length(), labels.length);
        this.dataset = dataset;
        this.labels = labels;
    }

    public LabeledPoint get(int i) {
        return new LabeledPoint(dataset.get(i), labels[i]);
    }

    public int length() {
        return labels.length;
    }

    public int dim() {
        return dataset.dim();
    }

    public boolean isEmpty() {
        return dataset.isEmpty();
    }

    public Matrix getData() {
        return dataset.data;
    }

    public LabeledDataset copyWithSameIndexesAndLabels(Matrix data) {
        return new LabeledDataset(dataset.indexes, data, labels);
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
        return super.equals(o);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
