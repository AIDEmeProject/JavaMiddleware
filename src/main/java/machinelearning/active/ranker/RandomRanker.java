package machinelearning.active.ranker;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.active.Ranker;
import utils.RandomState;
import utils.linalg.Vector;

import java.util.Random;

public class RandomRanker implements Ranker {

    private Random rnd = RandomState.newInstance();

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        int size = unlabeledData.length();
        double[] indexes = new double[size];

        for (int i = 0; i < size; i++) {
            indexes[i] = (double) i;
        }

        for (int i = size; i > 1; i--) {
            swap(indexes, i - 1, rnd.nextInt(i));
        }

        return Vector.FACTORY.make(indexes);
    }

    private static void swap(double[] arr, int i, int j) {
        double tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * @return a random a point from the input collection
     */
    @Override
    public DataPoint top(IndexedDataset unlabeledSet) {
        return unlabeledSet.sample(1).get(0);
    }
}
