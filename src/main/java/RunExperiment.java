import explore.Experiment;
import explore.metrics.ConfusionMatrixCalculator;
import explore.metrics.MetricCalculator;
import explore.metrics.TargetSetAccuracyCalculator;
import io.FolderManager;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.SvmLearner;

public class RunExperiment {

    public static void main(String[] args) {
        MetricCalculator[] calculators = new MetricCalculator[]{
                new ConfusionMatrixCalculator(new SvmLearner(1e7, new GaussianKernel())),
                new TargetSetAccuracyCalculator()
        };

        Experiment experiment = new Experiment(new FolderManager("experiment/sdss_Q1_0.1%/Simple Margin C=1000/"));

        int fileId = experiment.run(10);
        experiment.resume(fileId, 20);
        experiment.evaluate(fileId, calculators);
    }
}
