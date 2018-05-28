import classifier.NearestNeighborsClassifier;
import learner.ActiveTreeSearch;
import learner.Learner;
import learner.UncertaintySampler;

import java.util.*;

public class RunExperiment {

    private static double[][] generateX(int numRows, int dim, int seed) {
        double[][] X = new double[numRows][dim];

        Random rand = new Random(seed);

        for (int i=0; i < numRows; i++){
            for (int j=0; j < dim ; j++){
                X[i][j] = rand.nextDouble();
            }
        }

        return X;
    }

    private static double norm(double a, double b){
        return Math.sqrt(a*a + b*b);
    }

    private static int[] generateY(double[][] X){
        int[] y = new int[X.length];

        for (int i=0; i < y.length; i++){
            double a = X[i][0], b = X[i][1];
            if(norm(a, b) < 0.25 || norm(a, 1-b) < 0.25 || norm(1-a, b) < 0.25 || norm(1-a, 1-b) < 0.25 || norm(a-0.5, b-0.5) < 0.25){
                y[i] = 1;
            }

        }

        return y;
    }

    private static double[] computeAccuracy(Collection<Integer> rows, int[] y){
        double sum = 0;
        for (int label : y) {
            sum += label;
        }

        double[] cumsum = new double[rows.size()];
        int i = 0;
        for(int row: rows){
            cumsum[i] = y[row];
            if(i > 0){
                cumsum[i] += cumsum[i-1];
            }
            i++;
        }

        for (i = 0; i < cumsum.length; i++) {
            cumsum[i] /= sum;
        }

        return cumsum;
    }

    public static void main(String[] args){
        double[][] X = generateX(1000, 2, 1);
        int[] y = generateY(X);

        NearestNeighborsClassifier clf = new NearestNeighborsClassifier(X, 10, 0.1);

        Learner learner;
        //learner = new RandomSampler(clf)
        //learner = new UncertaintySampler(clf);
        learner = new ActiveTreeSearch(clf, 2);

        Collection<Integer> rows = Explore.run(X, y, learner, 100);

        double[] cumsum = computeAccuracy(rows, y);

        System.out.println(rows);
        System.out.println(Arrays.toString(cumsum));
        //System.out.println(Arrays.toString(clf.predict(new LabeledData(X, y))));
        //System.out.println(Arrays.toString(y));
    }
}
