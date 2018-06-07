import classifier.Classifier;
import classifier.NearestNeighborsClassifier;
import classifier.SVM.Kernel;
import classifier.SVM.SvmClassifier;
import classifier.SVM.SvmParameterAdapter;
import explore.ExplorationMetrics;
import explore.Explore;
import metrics.ConfusionMatrixCalculator;
import metrics.MetricCalculator;
import metrics.TargetSetAccuracyCalculator;
import sampling.StratifiedSampler;
import learner.*;

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

    public static void main(String[] args){
        // DATA
        double[][] X = generateX(250, 2, 1);
        int[] y = generateY(X);

        // CLASSIFIER
        SvmParameterAdapter params = new SvmParameterAdapter();
        params = params.C(1000).kernel(new Kernel()).probability(false);
        Classifier clf = new SvmClassifier(params);

        // LEARNER
        Learner learner;
        //learner = new RandomSampler(clf);
        //learner = new UncertaintySampler(clf);
        learner = new ActiveTreeSearch(new NearestNeighborsClassifier(X, 5, 0.1), 1);
        //learner = new SimpleMargin(new SvmClassifier(params));

        // METRICS
        Collection<MetricCalculator> metricCalculators = new ArrayList<>();
        metricCalculators.add(new ConfusionMatrixCalculator());
        metricCalculators.add(new TargetSetAccuracyCalculator());

        // INITIAL SAMPLING
        StratifiedSampler initialSampler = new StratifiedSampler(1, 1);

        // EXPLORE
        Explore explore = new Explore(initialSampler, 100, metricCalculators);
        ExplorationMetrics metrics = explore.averageRun(X, y, learner, 5, new long[] {1,2,3,4,5});

        // METRICS
        for (Map<String, Double> metric : metrics.getMetrics()){
            System.out.println(metric);
        }
    }
}
