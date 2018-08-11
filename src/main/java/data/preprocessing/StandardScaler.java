package data.preprocessing;

import data.DataPoint;
import utils.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class is responsible for standardizing each column of a double matrix; in other words, after processing, each
 * column will have mean zero and unit variance. Mean and standard deviation computation is based on Welford's method,
 * also present on Knuth's The Art Of Computer Programming book, Vol 2.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance">Variance computation methods wiki</a>
 */
public class StandardScaler {
    /**
     * Mean of each column
     */
    private double[] mean;

    /**
     * Standard deviation of each column
     */
    private double[] std;

    /**
     * number of points being fit
     */
    private int counter;

    /**
     * @return whether the fit() method was already called
     */
    public boolean isFit(){
        return mean != null && std != null;
    }

    /**
     * Fit the object to a particular data matrix. Basically, we compute and store the mean and standard deviation of
     * each column of the input matrix.
     * @param points: data matrix to fit
     */
    public void fit(Collection<DataPoint> points){
        Validator.assertNotEmpty(points);

        Iterator<DataPoint> pointIterator = points.iterator();

        // initialize mean and std
        DataPoint point = pointIterator.next();
        mean = Arrays.copyOf(point.getData(), point.getDim());
        std = new double[point.getDim()];

        // update mean and variance
        counter = 1;
        pointIterator.forEachRemaining(pt -> updateMeanAndStd(pt.getData()));

        // compute std from variance
        for (int j = 0; j < std.length; j++) {
            if (std[j] == 0){
                throw new IllegalArgumentException("Column number " + j + "has zero standard deviation.");
            }

            std[j] = Math.sqrt(std[j] / counter);
        }
    }

    private void updateMeanAndStd(double[] values){
        Validator.assertEqualLengths(mean, values);

        counter++;

        for (int j = 0; j < values.length; j++) {
            double diff = values[j] - mean[j];
            mean[j] += diff / counter;
            std[j] += diff * (values[j] - mean[j]);
        }
    }

    /**
     * Standardize a given collection of data points. The fit() method must have been called beforehand.
     * @param X: data to standardize
     * @return a new standardized collection of points
     * @throws RuntimeException if object was not fit beforehand
     * @throws IllegalArgumentException if data points have different dimension from expected
     */
    public Collection<DataPoint> transform(Collection<DataPoint> X){
        if (!isFit()){
            throw new RuntimeException("Object was not fit; remember to call fit() before calling transform().");
        }

        Collection<DataPoint> scaled = new ArrayList<>(X.size());

        for (DataPoint point : X){
            scaled.add(transform(point));
        }

        return scaled;
    }

    DataPoint transform(DataPoint point){
        Validator.assertEquals(point.getDim(), mean.length);

        double[] data = point.getData();
        double[] scaledData = new double[data.length];

        for (int j = 0; j < data.length; j++) {
            scaledData[j] = (data[j] - mean[j]) / std[j];
        }

        return new DataPoint(point.getRow(), point.getId(), scaledData);
    }
}
