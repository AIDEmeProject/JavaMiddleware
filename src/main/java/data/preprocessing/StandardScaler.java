package data.preprocessing;

import data.DataPoint;
import explore.statistics.Statistics;
import utils.Validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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

    private StandardScaler(double[] mean, double[] std) {
        this.mean = mean;
        this.std = std;
    }

    /**
     * Compute the mean and standard deviation of each column in the input collection
     * @param points: collection of points to fit
     * @return a Standard Scaler object fitted on the input data
     * @throws IllegalArgumentException if points is empty, or if any two points have different dimensions, or if
     * standard deviation of any column is zero
     */
    public static StandardScaler fit(List<DataPoint> points){
        Validator.assertNotEmpty(points);

        Statistics[] statistics = computeStatisticsOfEachFeature(points);

        double[] mean = getMeanArrayFromStatistics(statistics);
        double[] std = getStandardDeviationArrayFromStatistics(statistics);

        return new StandardScaler(mean, std);
    }

    public static List<DataPoint> fitAndTransform(List<DataPoint> points) {
        return fit(points).transform(points);
    }

    private static Statistics[] computeStatisticsOfEachFeature(Collection<DataPoint> points) {
        Iterator<DataPoint> pointIterator = points.iterator();

        DataPoint point = pointIterator.next();
        int dim = point.getDim();

        Statistics[] statistics = new Statistics[dim];
        for (int i = 0; i < dim; i++) {
            statistics[i] = new Statistics("column", point.get(i));
        }

        pointIterator.forEachRemaining(pt -> updateStatisticsGivenNewDataPoint(pt, statistics));

        return statistics;
    }

    private static void updateStatisticsGivenNewDataPoint(DataPoint point, Statistics[] statistics) {
        Validator.assertEquals(point.getDim(), statistics.length);

        for (int i = 0; i < statistics.length; i++) {
            statistics[i].update(point.get(i));
        }
    }

    private static double[] getMeanArrayFromStatistics(Statistics[] statistics) {
        double[] mean = new double[statistics.length];

        for (int i = 0; i < statistics.length; i++) {
            mean[i] = statistics[i].getMean();
        }

        return mean;
    }

    private static double[] getStandardDeviationArrayFromStatistics(Statistics[] statistics) {
        double[] std = new double[statistics.length];

        for (int i = 0; i < statistics.length; i++) {
            std[i] = statistics[i].getStandardDeviation();
            validateStandardDeviation(std[i], i);
        }

        return std;
    }

    private static void validateStandardDeviation(double standardDeviation, int column) {
        if (standardDeviation == 0){
            throw new IllegalArgumentException("Column number " + column + " has zero standard deviation.");
        }
    }

    /**
     * Standardize a given collection of data points.
     * @param dataPoints: data to standardize
     * @return a new standardized collection of points
     * @throws IllegalArgumentException if data points have different dimension from fitted data
     */
    public List<DataPoint> transform(List<DataPoint> dataPoints){
        return dataPoints.stream().map(this::transform).collect(Collectors.toList());
    }

    private DataPoint transform(DataPoint point){
        Validator.assertEquals(point.getDim(), mean.length);

        double[] scaledData = new double[point.getDim()];

        for (int j = 0; j < point.getDim(); j++) {
            scaledData[j] = (point.get(j) - mean[j]) / std[j];
        }

        return point.clone(scaledData);
    }
}
