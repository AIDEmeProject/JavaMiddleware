package data;

import explore.sampling.ReservoirSampler;
import utils.linalg.Matrix;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IndexedDataset implements Iterable<DataPoint> {
    private List<Long> indexes;
    private Matrix data;

    public static class Builder {
        private List<Long> indexes;
        private List<double[]> points;

        public Builder() {
            indexes = new ArrayList<>();
            points = new ArrayList<>();
        }

        public void add(long index, double[] point) {
            indexes.add(index);
            points.add(point);
        }

        public void add(DataPoint dataPoint) {
            indexes.add(dataPoint.id);
            points.add(dataPoint.data.toArray());
        }

        public IndexedDataset build() {
            double[][] matrix = points.toArray(new double[0][]);
            return new IndexedDataset(indexes, Matrix.FACTORY.make(matrix));
        }
    }

    public IndexedDataset(List<Long> indexes, Matrix data) {
        if (indexes.size() != data.numRows()) {
            throw new IllegalArgumentException("Incompatible sizes of indexes and matrix data.");
        }

        this.indexes = indexes;
        this.data = data;
    }

    public Matrix getData() {
        return data;
    }

    public boolean isEmpty() {
        return indexes.isEmpty();
    }

    public int length() {
        return indexes.size();
    }

    public DataPoint get(int i) {
        return new DataPoint(indexes.get(i), data.getRow(i));
    }

    public IndexedDataset getRows(int... rows) {
        List<Long> sliceIndexes = new ArrayList<>(rows.length);
        Arrays.stream(rows).forEach(row -> sliceIndexes.add(indexes.get(row)));
        return new IndexedDataset(sliceIndexes, data.getRows(rows));
    }

    public void swap(int row1, int row2) {
        Collections.swap(indexes, row1, row2);
        data.swapRows(row1, row2);
    }

    public IndexedDataset getRange(int from, int to) {
        return new IndexedDataset(indexes.subList(from, to), data.getRowSlice(from, to));
    }

    public IndexedDataset sample(int sampleSize) {
        if (sampleSize >= length()) {
            return this;
        }

        List<Integer> allRows = new ArrayList<>();
        IntStream.range(0, length()).forEach(allRows::add);
        Collection<Integer> rows = ReservoirSampler.sample(allRows, sampleSize);

        int[] sampledRows = new int[rows.size()];
        int i = 0;
        for (Integer row : rows) {
            sampledRows[i++] = row;
        }
        return getRows(sampledRows);
    }

    public IndexedDataset copy() {
        return new IndexedDataset(new ArrayList<>(indexes), data.copy());
    }

    public IndexedDataset copyWithSameIndexes(Matrix data) {
        return new IndexedDataset(indexes, data);
    }

    @Override
    public Iterator<DataPoint> iterator() {
        return new Iterator<DataPoint>() {
            int row = 0;

            @Override
            public boolean hasNext() {
                return row < indexes.size();
            }

            @Override
            public DataPoint next() {
                return get(row++);
            }
        };
    }

    public Stream<DataPoint> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexedDataset that = (IndexedDataset) o;
        return Objects.equals(indexes, that.indexes) &&
                Objects.equals(data, that.data);
    }

    @Override
    public String toString() {
        return "IndexedDataset{" +
                "indexes=" + indexes +
                ", data=" + data +
                '}';
    }
}
