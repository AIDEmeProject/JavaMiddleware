import classifier.BoundedClassifier;
import classifier.Classifier;
import classifier.NearestNeighborsClassifier;
import classifier.SVM.Kernel;
import classifier.SVM.SvmClassifier;
import classifier.SVM.SvmParameterAdapter;
import learner.*;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;

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
        // DATA
        double[][] X = generateX(250, 2, 1);
        int[] y = generateY(X);

        // CLASSIFIER
        Classifier clf;

        // svm
        SvmParameterAdapter params = new SvmParameterAdapter();
        params = params.C(1000).kernel(new Kernel()).probability(false);
        clf = new SvmClassifier(params);

        // knn
        //BoundedClassifier clf = new NearestNeighborsClassifier(X, 5, 0.1);

        // LEARNER
        Learner learner;
        //learner = new RandomSampler(clf);  // random
        //learner = new UncertaintySampler(clf);  // uncertainty sampling
        //learner = new ActiveTreeSearch(clf, 1);  // active search
        learner = new SimpleMargin(new SvmClassifier(params));

        // EXPLORE

        // initial sampling
        StratifiedSampler initialSampler = new StratifiedSampler(1, 1); // initial sampler

        // run exploration
        Explore explore = new Explore(initialSampler, 100);
        Collection<HashMap<String, Double>> metrics = explore.run(X, y, learner, 0);

        // METRICS
        for (HashMap<String, Double> metric : metrics){
            System.out.println(metric);
        }
//        double[] cumsum = computeAccuracy(rows, y);
//
//        System.out.println(rows);
//        System.out.println(Arrays.toString(cumsum));
    }
}
