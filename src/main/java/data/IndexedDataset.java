package data;

import explore.sampling.ReservoirSampler;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

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
    private final List<Long> indexes;

    /**
     * The underlying data (each row is a data point)
     */
    private Matrix data;

    private Matrix[] partitionedData;

    private int[][] partitionIndexes;

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

        public Matrix buildMatrix(){
            double[][] matrix = points.toArray(new double[0][]);
            return Matrix.FACTORY.make(matrix);
        }
    }

    /**
     * @param indexes: index of each data point
     * @param data: feature matrix (each row represents a data point)
     * @throws IllegalArgumentException if indexes.size() is different from data.rows()
     */
    public IndexedDataset(List<Long> indexes, Matrix data) {
        this(indexes, data, new Matrix[]{data}, new int[][] {IntStream.range(0, data.cols()).toArray()});
    }

    private IndexedDataset(List<Long> indexes, Matrix data, Matrix[] partitionedData, int[][] partitionIndexes) {
        Validator.assertEquals(indexes.size(), data.rows());
        Validator.assertEqualLengths(partitionedData, partitionIndexes);

        this.indexes = indexes;
        this.data = data;
        this.partitionedData = partitionedData;
        this.partitionIndexes = partitionIndexes;
    }

    private List<Long> secondaryIndex;

    public void setSecondaryIndex(List<Long> secondaryIndex) {
        this.secondaryIndex = secondaryIndex;
    }

    /**
     * @param index: secondary index to retrieve
     * @return DataPoint matching the secondary index
     */
    public DataPoint getFromSecondaryIndex(long index) {
        return get(secondaryIndex.indexOf(index));
    }

    /**
     * @param indexes: secondary indexes to retrieve
     * @return an IndexedDataset containing all points matching the secondary indexes in the input collection.
     */
    public IndexedDataset getFromSecondaryIndex(Collection<Long> indexes) {
        Builder builder = new Builder();

        for (Long index : indexes) {
            builder.add(getFromSecondaryIndex(index));
        }

        return builder.build();
    }

    /**
     * Add datapoint to Matrix
     * @param dataPoint
     * @return
     */
    public void add(Vector dataPoint) {

        long nPoint = this.indexes.size();
        //indexes.add(nPoint + 1);
        this.indexes.add((int) nPoint + 1, nPoint + 1);
        Builder builder = new Builder();

        Vector row;
        DataPoint point;
        for (int i =0 ; i < nPoint; i++){
            row = this.data.getRow(i);

            point = new DataPoint(i, row);
            builder.add(point);

        }

        this.data = builder.buildMatrix();

    }


    public DataPoint getFakeData(){
        return this.get(1);
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
     * @param index: index of data point to be retrieved
     * @return the data point corresponding to the specified index
     */
    public DataPoint getFromIndex(long index) {
        return get(indexes.indexOf(index));
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

        Matrix filteredData = data.getRows(rows);
        Matrix[] partition = partitionSize() == 1 ? new Matrix[] {filteredData} : Arrays.stream(partitionedData).map(x -> x.getRows(rows)).toArray(Matrix[]::new);

        return new IndexedDataset(sliceIndexes, filteredData, partition, partitionIndexes);
    }

    /**
     * @param from: start index (inclusive)
     * @param to: end index (exclusive)
     * @return an IndexDataset only containing the range of specified rows
     * @throws IndexOutOfBoundsException if indexes are out-of-bounds or {@code from} is not smaller than {@code to}
     */
    IndexedDataset getRange(int from, int to) {
        Matrix filteredData = data.getRowSlice(from, to);
        Matrix[] partition = partitionSize() == 1 ? new Matrix[] {filteredData} : Arrays.stream(partitionedData).map(x -> x.getRowSlice(from, to)).toArray(Matrix[]::new);
        return new IndexedDataset(indexes.subList(from, to), filteredData, partition, partitionIndexes);
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
        if (partitionSize() > 1)
            Arrays.stream(partitionedData).forEach(x -> x.swapRows(row1, row2));
    }

    public IndexedDataset append(IndexedDataset data) {
        Validator.assertEquals(dim(), data.dim());

        Builder builder = new Builder();
        for (DataPoint point: this) {
            builder.add(point);
        }
        for (DataPoint point: data) {
            builder.add(point);
        }
        return builder.build();
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
        Matrix copiedData = data.copy();
        Matrix[] partition = partitionSize() == 1 ? new Matrix[] {copiedData} : Arrays.stream(partitionedData).map(Matrix::copy).toArray(Matrix[]::new);
        return new IndexedDataset(new ArrayList<>(indexes), copiedData, partition, partitionIndexes);
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

    public void setFactorizationStructure(int[][] partition) {
        int size = partition.length;

        if (size > 1) {
            partitionedData = new Matrix[size];
            for (int i = 0; i < size; i++) {
                partitionedData[i] = data.getCols(partition[i]);
            }

            partitionIndexes = partition.clone();
        }
    }

    public IndexedDataset[] getPartitionedData() {
        return Arrays.stream(partitionedData).map(data -> new IndexedDataset(indexes, data)).toArray(IndexedDataset[]::new);
    }

    public int partitionSize() {
        return partitionedData.length;
    }

    public int[][] getPartitionIndexes() {
        return partitionIndexes;
    }

    public boolean hasFactorizationStructure() {
        return partitionSize() > 1;
    }

    @Override
    public String toString() {
        return "IndexedDataset{" +
                "indexes=" + indexes + '\n' +
                ", secondaryIndex=" + secondaryIndex + '\n' +
                ", data=" + data +
                '}';
    }
}
