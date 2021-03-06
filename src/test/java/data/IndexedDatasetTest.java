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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndexedDatasetTest {
    private List<Long> indexes;
    private Matrix data;
    private IndexedDataset dataset;

    @BeforeEach
    void setUp() {
        indexes = Arrays.asList(0L, 10L, 20L);
        data = Matrix.FACTORY.make(3, 2, 1, 2, 3, 4, 5, 6);  // [[1,2], [3,4], [5, 6]]
        dataset = new IndexedDataset(indexes, data);
    }

    @Test
    void build_noDataPointsAdded_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void build_pointsOfDifferentDimensionsAdded_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0L, new double[1]);
        builder.add(1L, new double[2]);
        assertThrows(RuntimeException.class, builder::build);
    }

    @Test
    void build_compatiblePoints_expectedDatasetConstructed() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0L, new double[] {1, 2});
        builder.add(10L, new double[] {3, 4});
        builder.add(20L, new double[] {5, 6});
        assertEquals(dataset, builder.build());
    }

    @Test
    void constructor_indexesAndMatrixHaveDifferentSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new IndexedDataset(Arrays.asList(1L, 2L), data));
    }

    @Test
    void length_datasetContainsThreeDataPoints_returnsThree() {
        assertEquals(data.rows(), dataset.length());
    }

    @Test
    void dim_dataPointsAreTwoDimensional_returnsTwo() {
        assertEquals(data.cols(), dataset.dim());
    }

    @Test
    void get_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.get(-1));
    }

    @Test
    void get_indexEqualsToLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.get(dataset.length()));
    }

    @Test
    void get_indexInBounds_returnsExpectedDataPoint() {
        assertEquals(new DataPoint(0L, Vector.FACTORY.make(1, 2)), dataset.get(0));
        assertEquals(new DataPoint(10L, Vector.FACTORY.make(3, 4)), dataset.get(1));
        assertEquals(new DataPoint(20L, Vector.FACTORY.make(5, 6)), dataset.get(2));
    }

    @Test
    void getRows_emptyIndexesArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getRows());
    }

    @Test
    void getRows_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRows(-1));
    }

    @Test
    void getRows_indexEqualsToLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRows(dataset.length()));
    }

    @Test
    void getRows_indexInBounds_returnsExpectedIndexedDataset() {
        IndexedDataset expected = new IndexedDataset(Arrays.asList(20L, 10L), Matrix.FACTORY.make(2, 2, 5, 6, 3, 4));
        assertEquals(expected, dataset.getRows(2, 1));
    }

    @Test
    void getRows_withFactorizationStructure_partitionsCorrectlyFiltered() {
        int[] rows = new int[] {2, 1};
        dataset.setFactorizationStructure(new int[][] {{0}, {1}});
        IndexedDataset result = dataset.getRows(rows);

        List<Long> idx = Arrays.asList(20L, 10L);
        assertEquals(new IndexedDataset(idx, data.getCols(0).getRows(rows)), result.getPartitionedData()[0]);
        assertEquals(new IndexedDataset(idx, data.getCols(1).getRows(rows)), result.getPartitionedData()[1]);
    }

    @Test
    void getRange_negativeIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getRange(-1, 0));
    }

    @Test
    void getRange_indexLargerThanLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getRows(0, dataset.length()+1));
    }

    @Test
    void getRange_indexInBounds_returnsExpectedIndexedDataset() {
        IndexedDataset expected = new IndexedDataset(Arrays.asList(10L, 20L), Matrix.FACTORY.make(2, 2, 3, 4, 5, 6));
        assertEquals(expected, dataset.getRange(1, 3));
    }

    @Test
    void getRange_withFactorizationStructure_partitionsCorrectlyFiltered() {
        dataset.setFactorizationStructure(new int[][] {{0}, {1}});
        IndexedDataset result = dataset.getRange(1, 3);

        List<Long> idx = Arrays.asList(10L, 20L);
        assertEquals(new IndexedDataset(idx, data.getCols(0).getRowSlice(1, 3)), result.getPartitionedData()[0]);
        assertEquals(new IndexedDataset(idx, data.getCols(1).getRowSlice(1, 3)), result.getPartitionedData()[1]);
    }

    @Test
    void swap_negativeIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(0, -1));
    }

    @Test
    void swap_indexEqualsToLength_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(dataset.length(), 0));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.swap(0, dataset.length()));
    }

    @Test
    void swap_identicalIndexes_datasetRemainsUnchanged() {
        IndexedDataset copy = dataset.copy();
        dataset.swap(0, 0);
        assertEquals(copy, dataset);
    }

    @Test
    void swap_differentIndexesInRange_datasetRowsCorrectlySwapped() {
        IndexedDataset expected = new IndexedDataset(Arrays.asList(10L, 0L, 20L), Matrix.FACTORY.make(3, 2,  3, 4, 1, 2, 5, 6));
        dataset.swap(0, 1);
        assertEquals(expected, dataset);
    }

    @Test
    void swap_withFactorizationStructure_partitionsCorrectlySwapped() {
        dataset.setFactorizationStructure(new int[][] {{0}, {1}});
        dataset.swap(0, 1);

        List<Long> idx = Arrays.asList(10L, 0L, 20L);
        assertEquals(new IndexedDataset(idx, Matrix.FACTORY.make(3, 1,  3, 1, 5)), dataset.getPartitionedData()[0]);
        assertEquals(new IndexedDataset(idx, Matrix.FACTORY.make(3, 1,  4, 2, 6)), dataset.getPartitionedData()[1]);
    }

    @Test
    void sample_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.sample(-1));
    }

    @Test
    void sample_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.sample(0));
    }

    @Test
    void sample_sampleSizeEqualsLength_returnsTheOwnDataset() {
        assertSame(dataset, dataset.sample(dataset.length()));
    }

    @Test
    void sample_sampleSizeSmallerThanLength_returnsDatasetOfExpectedSize() {
        assertEquals(2, dataset.sample(2).length());
    }

    @Test
    void copy_swapCopyRows_originalDatasetRemainsUnchanged() {
        IndexedDataset copy = dataset.copy();
        dataset.copy().swap(0, 1);
        assertEquals(copy, dataset);
    }

    @Test
    void copyWithSameIndexes_newMatrixHasDifferentLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.copyWithSameIndexes(Matrix.FACTORY.zeros(1, 2)));
    }

    @Test
    void copyWithSameIndexes_compatibleMatrix_returnsDatasetWithNewDataButSameIndexes() {
        Matrix matrix = Matrix.FACTORY.make(3, 1, 10, 11, 12);
        IndexedDataset newDataset = dataset.copyWithSameIndexes(matrix);
        assertEquals(indexes, newDataset.getIndexes());
        assertEquals(matrix, newDataset.getData());
    }

    @Test
    void append_dataOfIncompatibleDimension_throwsException() {;
        assertThrows(IllegalArgumentException.class, () -> dataset.append(new IndexedDataset(Arrays.asList(30L), Matrix.FACTORY.make(1, 3, 7, 8, 9))));
    }

    @Test
    void append_newData_returnsNewDatasetWithNewDataAppendedToEnd() {
        IndexedDataset newData = new IndexedDataset(Arrays.asList(30L, 40L), Matrix.FACTORY.make(2, 2, 7, 8, 9, 10));
        assertEquals(new IndexedDataset(Arrays.asList(0L, 10L, 20L, 30L, 40L), Matrix.FACTORY.make(5, 2, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)), dataset.append(newData));
    }

    @Test
    void toListCorrectlyConvertsTheDataset() {
        List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < dataset.length(); i++) {
            dataPoints.add(dataset.get(i));
        }
        assertEquals(dataPoints, dataset.toList());
    }
}