import io.DatabaseReader;
import io.IniConfigurationParser;

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
//        // DATA
//        double[][] X = generateX(250, 2, 1);
//        int[] y = generateY(X);
//
//        // CLASSIFIER
//        // svm
//        SvmParameterAdapter params = new SvmParameterAdapter();
//        params = params
//                .C(1000)
//                .kernel(new Kernel())
//                .probability(true);
//        Learner learner = new SvmLearner(params);
//
//        // knn
//        BoundedLearner boundedLearner = new NearestNeighborsLearner(X, 5, 0.1);
//
//        // LEARNER
//        Map<String, ActiveLearner> activeLearners = new HashMap<>();
//        activeLearners.put("Random Learner kNN", new RandomSampler(boundedLearner));
//        activeLearners.put("Uncertainty Sampling kNN", new UncertaintySampler(boundedLearner));
//        activeLearners.put("Active Tree Search l=1 kNN", new ActiveTreeSearch(boundedLearner, 1));
//        activeLearners.put("Active Tree Search l=2 kNN", new ActiveTreeSearch(boundedLearner, 2));
//        //activeLearners.add(new SimpleMargin(new SvmLearner(params)));
//
//        // METRICS
//        Collection<MetricCalculator> metricCalculators = new ArrayList<>();
//        metricCalculators.add(new ConfusionMatrixCalculator());
//        metricCalculators.add(new TargetSetAccuracyCalculator());
//
//        // INITIAL SAMPLING
//        StratifiedSampler initialSampler = new StratifiedSampler(1, 1);
//
//        // EXPLORE
//        Explore explore = new Explore(initialSampler, 100, 10, metricCalculators);
//
//        for (Map.Entry<String, ActiveLearner> entry : activeLearners.entrySet()) {
//            ExplorationMetrics metrics = explore.averageRun(X, y, entry.getValue(), 10); //, new long[]{1, 2, 3, 4, 5,}
//            MetricWriter.write(metrics, "./experiment/" + entry.getKey() + ".csv");
//        }
//
        String dataset = "sdss_random_sample";
        String predicate = "rowc > 662.5 AND rowc < 702.5 AND colc > 991.5 AND colc < 1053.5";
        ArrayList<String> columns = new ArrayList<>();
        columns.add("rowc");
        columns.add("colc");

        IniConfigurationParser parser = new IniConfigurationParser("datasets");
        Map<String, String> datasetConfig = parser.read(dataset);
        columns.add(datasetConfig.get("key"));

        parser = new IniConfigurationParser("connections");
        Map<String, String> connectionConfig = parser.read(datasetConfig.get("connection"));

        DatabaseReader reader = new DatabaseReader(
                connectionConfig.get("url"),
                datasetConfig.get("database"),
                connectionConfig.get("user"),
                connectionConfig.get("password"));

        double[][] X = reader.read(datasetConfig.get("table"), columns.toArray(new String[columns.size()]));
        Set<Long> positiveKeys = reader.readKeys(datasetConfig.get("table"), datasetConfig.get("key"), predicate);
        int[] y = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            y[i] = positiveKeys.contains((long) X[i][2]) ? 1 : 0;
        }

        System.out.println(datasetConfig);
        System.out.println(connectionConfig);
        System.out.println(X.length);
        System.out.println(X[0].length);
        System.out.println(positiveKeys.size());
        //System.out.println(Arrays.deepToString(X));
    }
}
