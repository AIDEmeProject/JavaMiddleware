import classifier.Classifier;
import classifier.NearestNeighborsClassifier;
import classifier.SVM.Kernel;
import classifier.SVM.SvmClassifier;
import classifier.SVM.SvmParameterAdapter;
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

    private static Map<String, Double> sumMaps(Map<String, Double> map1, Map<String, Double> map2){
        if (!map1.keySet().equals(map2.keySet())){
            throw new IllegalArgumentException("Maps must have the same key set.");
        }

        Map<String, Double> result = new HashMap<>();
        for (String key : map1.keySet()){
            result.put(key, map1.get(key) + map2.get(key));
        }
        return result;
    }

    private static List<Map<String, Double>> sumListMaps(List<Map<String, Double>> list1, List<Map<String, Double>> list2){
        List<Map<String, Double>> result = new ArrayList<>();
        Iterator<Map<String, Double>> it1 = list1.iterator();
        Iterator<Map<String, Double>> it2 = list2.iterator();

        while (it1.hasNext() && it2.hasNext()){
            result.add(sumMaps(it1.next(), it2.next()));
        }

        if (it1.hasNext() || it2.hasNext()){
            throw new IllegalArgumentException("Lists should have the same number of elements.");
        }

        return result;
    }

    private static Map<String, Double> divideMap(Map<String, Double> map, int denominator){
        if (denominator == 0){
            throw new IllegalArgumentException("Dividing by zero.");
        }

        Map<String, Double> result = new HashMap<>();

        for (String key : map.keySet()){
            result.put(key, map.get(key) / denominator);
        }

        return result;
    }

    private static List<Map<String, Double>> divideListMaps(List<Map<String, Double>> list, int denominator){
        List<Map<String, Double>> result = new ArrayList<>();

        for (Map<String, Double> map : list){
            result.add(divideMap(map, denominator));
        }

        return result;
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
        int runs = 10;
        Explore explore = new Explore(initialSampler, 100, metricCalculators);

        List<Map<String, Double>> metrics = explore.run(X, y, learner, 0);

        for (int i = 1; i < runs; i++) {
            metrics = sumListMaps(metrics, explore.run(X, y, learner, i+1));
        }

        metrics = divideListMaps(metrics, runs);

        // METRICS
        for (Map<String, Double> metric : metrics){
            System.out.println(metric);
        }
    }
}
