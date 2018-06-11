package data;

import java.util.Set;

/**
 * This class holds two elements: a dataset and a collection of its row indexes. It only contains getters for its data.
 */
public class IndexedDataset {
    private final double[][] data;
    private final Set<Long> indexes;

    public IndexedDataset(Set<Long> indexes, double[][] data) {
        if (indexes.size() != data.length){
            throw new IllegalArgumentException("Indexes and data have incompatible sizes.");
        }
        this.data = data;
        this.indexes = indexes;
    }

    public double[][] getData() {
        return data;
    }

    public Set<Long> getIndexes() {
        return indexes;
    }
}
