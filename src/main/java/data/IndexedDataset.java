package data;

import java.util.LinkedHashSet;

public class IndexedDataset {
    private final LinkedHashSet<Long> indexes;
    private final double[][] data;

    public IndexedDataset(LinkedHashSet<Long> indexes, double[][] data) {
        this.indexes = indexes;
        this.data = data;
    }

    public LinkedHashSet<Long> getIndexes() {
        return indexes;
    }

    public double[][] getData() {
        return data;
    }
}
