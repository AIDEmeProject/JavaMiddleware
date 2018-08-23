import data.DataPoint;
import data.preprocessing.StandardScaler;
import explore.Explore;
import explore.metrics.ConfusionMatrixCalculator;
import explore.metrics.MetricCalculator;
import explore.metrics.TargetSetAccuracyCalculator;
import explore.sampling.StratifiedSampler;
import explore.user.DummyUser;
import explore.user.User;
import io.FolderManager;
import machinelearning.active.ActiveLearner;
import machinelearning.active.learning.GeneralizedBinarySearch;
import machinelearning.active.learning.versionspace.KernelVersionSpace;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.active.learning.versionspace.convexbody.SampleCache;
import machinelearning.active.learning.versionspace.convexbody.sampling.RoundingAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunSampler;
import machinelearning.classifier.MajorityVoteLearner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.SvmLearner;
import utils.linprog.LinearProgramSolver;

import java.io.File;
import java.util.*;

public class RunExperiment {

    private static Collection<DataPoint> generateX(int numRows, int dim, int seed) {
        Collection<DataPoint> points = new ArrayList<>(numRows);

        Random rand = new Random(seed);

        for (int i=0; i < numRows; i++){
            double[] point = new double[dim];
            for (int j=0; j < dim ; j++){
                point[j] = rand.nextDouble();
            }
            points.add(new DataPoint(i, point));
        }

        return points;
    }

    private static Set<Long> generateY(Collection<DataPoint> points){
        Set<Long> keys = new HashSet<>();

        for (DataPoint point : points){
            double[] data = point.getData();

            double a = data[0], b = data[1];

            if(norm(a, b) < 0.25 || norm(a, 1-b) < 0.25 || norm(1-a, b) < 0.25 || norm(1-a, 1-b) < 0.25 || norm(a-0.5, b-0.5) < 0.25){
                keys.add(point.getId());
            }
        }

        return keys;
    }

    private static double norm(double a, double b){
        return Math.sqrt(a*a + b*b);
    }

    public static void main(String[] args){
        // DATA and USER
        // simple example
        String task = "simple-10000";
        Collection<DataPoint> points = generateX(10000, 2, 1);
        Set<Long> y = generateY(points);
        User user = new DummyUser(y);

        // sdss
        //String task = "sdss_Q1_0.1%";
//        TaskReader reader = new TaskReader(task);
//        Collection<DataPoint> data = reader.readData();
//        Set<Long> positiveKeys = reader.readTargetSetKeys();
//        User user = new DummyUser(positiveKeys);

        // SCALING
        points = StandardScaler.fit(points).transform(points);

        // CLASSIFIER
        // svm
        SvmLearner svm = new SvmLearner(1000, new GaussianKernel());

        // ACTIVE LEARNER
        Map<String, ActiveLearner> activeLearners = new LinkedHashMap<>();
        //activeLearners.put("Random Learner svm", new RandomSampler(svm));
        //activeLearners.put("Simple Margin C=1000", new SimpleMargin(svm));

        HitAndRunSampler sampler = new HitAndRunSampler(100, 10, new RoundingAlgorithm());
        LinearVersionSpace linearVersionSpace = new LinearVersionSpace(sampler, LinearProgramSolver.getFactory(LinearProgramSolver.LIBRARY.OJALGO));
        linearVersionSpace.addIntercept();
        linearVersionSpace.setSampleCachingStrategy(new SampleCache());
        VersionSpace versionSpace = new KernelVersionSpace(linearVersionSpace, new GaussianKernel());
        MajorityVoteLearner majorityVoteLearner = new MajorityVoteLearner(versionSpace, 8);
        activeLearners.put("Linear GBS learner=MV warmup=100 thin=10 numSamples=8", new GeneralizedBinarySearch(majorityVoteLearner));

        // METRICS
        Collection<MetricCalculator> metricCalculators = new ArrayList<>();
        metricCalculators.add(new ConfusionMatrixCalculator());
        metricCalculators.add(new TargetSetAccuracyCalculator());

        // INITIAL SAMPLING
        StratifiedSampler initialSampler = new StratifiedSampler(1, 1);

        // EXPLORE
        int budget = 50;
        int runs = 1;
        Explore explore = new Explore(initialSampler, budget, metricCalculators);

        for (Map.Entry<String, ActiveLearner> entry : activeLearners.entrySet()) {
            System.out.println(entry.getKey());
            try {
                FolderManager folder = new FolderManager("experiment" + File.separator + task + File.separator + entry.getKey());
                explore.run(points, user, entry.getValue(), runs, new long[] {10}, folder);
                //StatisticsCalculator.averageRunFiles(folder.getRuns(), folder.createNewOutputFile());
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
