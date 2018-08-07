import active.ActiveLearner;
import active.learning.GeneralizedBinarySearch;
import active.learning.RandomSampler;
import active.learning.SimpleMargin;
import classifier.SVM.Kernel;
import classifier.SVM.SvmLearner;
import classifier.SVM.SvmParameterAdapter;
import classifier.linear.MajorityVoteLearner;
import data.DataPoint;
import explore.Explore;
import io.FolderManager;
import metrics.ConfusionMatrixCalculator;
import metrics.MetricCalculator;
import metrics.TargetSetAccuracyCalculator;
import preprocessing.StandardScaler;
import sampling.HitAndRunSampler;
import sampling.StratifiedSampler;
import user.DummyUser;
import user.User;
import utils.statistics.StatisticsCalculator;
import utils.versionspace.KernelVersionSpace;
import utils.versionspace.VersionSpace;

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
        StandardScaler scaler = new StandardScaler();
        scaler.fit(points);
        points = scaler.transform(points);

        // CLASSIFIER
        // svm
        SvmParameterAdapter params = new SvmParameterAdapter();
        params = params
                .C(1000)
                .kernel(new Kernel())
                .probability(false);
        SvmLearner svm = new SvmLearner(params);

        // ACTIVE LEARNER
        Map<String, ActiveLearner> activeLearners = new LinkedHashMap<>();
//        activeLearners.put("Random Learner svm", new RandomSampler(svm));
//        activeLearners.put("Simple Margin C=1000", new SimpleMargin(svm));

        HitAndRunSampler sampler = new HitAndRunSampler(100, 10);
        VersionSpace versionSpace = new KernelVersionSpace(sampler, true);
        MajorityVoteLearner majorityVoteLearner = new MajorityVoteLearner(versionSpace, 8);
        activeLearners.put("Linear GBS learner=SVM warmup=100 thin=10 numSamples=8", new GeneralizedBinarySearch(svm, majorityVoteLearner));

        //activeLearners.put("Simple Margin C=1000", new SimpleMargin(svm));

        // METRICS
        Collection<MetricCalculator> metricCalculators = new ArrayList<>();
        metricCalculators.add(new ConfusionMatrixCalculator());
        metricCalculators.add(new TargetSetAccuracyCalculator());

        // INITIAL SAMPLING
        StratifiedSampler initialSampler = new StratifiedSampler(1, 1);

        // EXPLORE
        Explore explore = new Explore(initialSampler, 50, metricCalculators);

        for (Map.Entry<String, ActiveLearner> entry : activeLearners.entrySet()) {
            System.out.println(entry.getKey());
            try {
                FolderManager folder = new FolderManager("experiment" + File.separator + task + File.separator + entry.getKey());
                explore.run(points, user, entry.getValue(), 1, folder);
                StatisticsCalculator.averageRunFiles(folder.getRuns(), folder.createNewOutputFile());
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
