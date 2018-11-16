package data;

import explore.sampling.ReservoirSampler;
import utils.Validator;
import utils.linalg.Matrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This module is a in-memory storage for data points. Its main functionality is to provide easy access to its data under
 * several different formats (iterator, stream, range, ...).
 *
 * @see DataPoint
 */
public class IndexedDataset implements Iterable<DataPoint> {
    /**
     * The index of each data point
     */
    private List<Long> indexes;

    /**
     * The underlying data (each row is a data point)
     */
    private Matrix data;

    /**
     * This Builder is a utility class for incrementally building an IndexedDataset object.
     */
    public static class Builder {
        private List<Long> indexes;
        private List<double[]> points;

        /**
         * Creates empty dataset
         */
        public Builder() {
            indexes = new ArrayList<>();
            points = new ArrayList<>();
        }

        /**
         * Appends new data point to the end of the dataset
         * @param index: data point's index
         * @param point: data point's data
         */
        public void add(long index, double[] point) {
            indexes.add(index);
            points.add(point);
        }

        /**
         * Appends new data point to the end of the dataset
         * @param dataPoint
         */
        public void add(DataPoint dataPoint) {
            indexes.add(dataPoint.getId());
            points.add(dataPoint.getData().toArray());
        }

        /**
         * @return an {@link IndexedDataset} built from the appended data
         */
        public IndexedDataset build() {
            Validator.assertNotEmpty(indexes);

            double[][] matrix = points.toArray(new double[0][]);
            return new IndexedDataset(indexes, Matrix.FACTORY.make(matrix));
        }
    }

    /**
     * @param indexes: index of each data point
     * @param data: feature matrix (each row represents a data point)
     * @throws IllegalArgumentException if indexes.size() is different from data.rows()
     */
    public IndexedDataset(List<Long> indexes, Matrix data) {
        Validator.assertEquals(indexes.size(), data.rows());

        this.indexes = indexes;
        this.data = data;
    }

    public List<Long> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public Matrix getData() {
        return data;
    }

    /**
     * @return number of data points
     */
    public int length() {
        return indexes.size();
    }

    /**
     * @return dimension of each data point
     */
    public int dim() {
        return data.cols();
    }

    /**
     * @param i: row index of data point to retrieve
     * @return the data point at row {@code i}.
     * @throws IndexOutOfBoundsException if {@code i} is out-of-bounds
     */
    public DataPoint get(int i) {
        return new DataPoint(indexes.get(i), data.getRow(i));
    }

    /**
     * @param rows: rows to be retrieved
     * @return an IndexedDataset containing only the specified rows (data will be copied in this process)
     * @throws IllegalArgumentException if rows is empty
     * @throws IndexOutOfBoundsException if any row is out-of-bounds
     */
    IndexedDataset getRows(int... rows) {
        List<Long> sliceIndexes = new ArrayList<>(rows.length);
        Arrays.stream(rows).forEach(row -> sliceIndexes.add(indexes.get(row)));
        return new IndexedDataset(sliceIndexes, data.getRows(rows));
    }

    /**
     * @param from: start index (inclusive)
     * @param to: end index (exclusive)
     * @return an IndexDataset only containing the range of specified rows
     * @throws IndexOutOfBoundsException if indexes are out-of-bounds or {@code from} is not smaller than {@code to}
     */
    IndexedDataset getRange(int from, int to) {
        return new IndexedDataset(indexes.subList(from, to), data.getRowSlice(from, to));
    }

    /**
     * Swaps two rows of the data
     * @param row1: index of row to swap
     * @param row2: index of another row to swap
     * @throws IndexOutOfBoundsException if any row is out-of-bounds
     */
    void swap(int row1, int row2) {
        Collections.swap(indexes, row1, row2);
        data.swapRows(row1, row2);
    }

    /**
     * @param sampleSize: size of random sample to be retrieved
     * @return a IndexedDataset containing a random sample of the original data. This own dataset will be returned if
     * sampleSize is larger than length().
     */
    public IndexedDataset sample(int sampleSize) {
        Validator.assertPositive(sampleSize);

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

    /**
     * @return a copy of this object
     */
    public IndexedDataset copy() {
        return new IndexedDataset(new ArrayList<>(indexes), data.copy());
    }

    /**
     * @param data: new features matrix
     * @return a new IndexedDataset object with same indexes as {@code this}, but with the underlying data matrix replaced
     * by the input one
     * @throws IllegalArgumentException if data.rows() is different from length()
     */
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

    public List<DataPoint> toList() {
        return stream().collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexedDataset that = (IndexedDataset) o;
        return Objects.equals(indexes, that.indexes) &&
                Objects.equals(data, that.data);
    }
}
