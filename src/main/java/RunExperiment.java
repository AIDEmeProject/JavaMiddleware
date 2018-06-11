import active.ActiveLearner;
import active.activelearning.RandomSampler;
import active.activelearning.SimpleMargin;
import active.activelearning.UncertaintySampler;
import active.activesearch.ActiveTreeSearch;
import classifier.BoundedLearner;
import classifier.Learner;
import classifier.SVM.Kernel;
import classifier.SVM.SvmLearner;
import classifier.SVM.SvmParameterAdapter;
import classifier.nearest_neighbors.NearestNeighborsLearner;
import data.IndexedDataset;
import explore.ExplorationMetrics;
import explore.Explore;
import io.MetricWriter;
import io.TaskReader;
import metrics.ConfusionMatrixCalculator;
import metrics.MetricCalculator;
import metrics.TargetSetAccuracyCalculator;
import sampling.StratifiedSampler;
import user.DummyUser;
import user.User;

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

    private static double norm(double a, double b){
        return Math.sqrt(a*a + b*b);
    }

    public static void main(String[] args){
        // DATA and USER
        // simple example
//        double[][] X = generateX(250, 2, 1);
//        int[] y = generateY(X);
//        User user = new DummyUser(y);

        // sdss
        TaskReader reader = new TaskReader("sdss_Q1_0.1%");
        IndexedDataset data = reader.readData();
        Set<Long> positiveKeys = reader.readTargetSetKeys();

        double[][] X = data.getData();
        User user = new DummyUser(data.getIndexes(), positiveKeys);

        // CLASSIFIER
        // svm
        SvmParameterAdapter params = new SvmParameterAdapter();
        params = params
                .C(1000)
                .kernel(new Kernel())
                .probability(true);
        Learner learner = new SvmLearner(params);

        // knn
        BoundedLearner boundedLearner = new NearestNeighborsLearner(X, 10, 0.1);

        // ACTIVE LEARNER
        Map<String, ActiveLearner> activeLearners = new HashMap<>();
        activeLearners.put("Random Learner kNN", new RandomSampler(boundedLearner));
        activeLearners.put("Uncertainty Sampling kNN", new UncertaintySampler(boundedLearner));
        activeLearners.put("Active Tree Search l=1 kNN", new ActiveTreeSearch(boundedLearner, 1));
        //activeLearners.put("Active Tree Search l=2 kNN", new ActiveTreeSearch(boundedLearner, 2));
        //activeLearners.put("Simple Margin C=1000", new SimpleMargin(learner));

        // METRICS
        Collection<MetricCalculator> metricCalculators = new ArrayList<>();
        metricCalculators.add(new ConfusionMatrixCalculator());
        metricCalculators.add(new TargetSetAccuracyCalculator());

        // INITIAL SAMPLING
        StratifiedSampler initialSampler = new StratifiedSampler(1, 1);

        // EXPLORE
        Explore explore = new Explore(initialSampler, 100, metricCalculators);

        for (Map.Entry<String, ActiveLearner> entry : activeLearners.entrySet()) {
            System.out.println(entry.getKey());
            ExplorationMetrics metrics = explore.averageRun(X, user, entry.getValue(), 1); //, new long[]{1, 2, 3, 4, 5,}
            MetricWriter.write(metrics, "./experiment/" + entry.getKey() + ".csv");
        }
    }
}
