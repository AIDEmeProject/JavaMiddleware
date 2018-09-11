import explore.Experiment;
import explore.metrics.MetricCalculator;
import io.CommandLineArguments;
import io.FolderManager;

public class RunExperiment {

    public static void main(String[] args) {
        CommandLineArguments arguments = CommandLineArguments.parseCommandLineArgs(args);
        System.out.println(arguments);

        FolderManager experimentFolder = new FolderManager(arguments.getExperimentDirectory());
        Experiment experiment = new Experiment(experimentFolder);

        switch (arguments.getMode().toUpperCase()) {
            case "NEW":
                for (int i = 0; i < arguments.getNumRuns(); i++) {
                    experiment.run(arguments.getBudget());
                }
                break;
            case "RESUME":
                for (int id : arguments.getRuns()) {
                    experiment.resume(id, arguments.getBudget());
                }
                break;
            case "EVAL":
                MetricCalculator[] metricCalculators = new MetricCalculator[arguments.getMetrics().size()];

                int i = 0;
                for (String metric : arguments.getMetrics()) {
                    metricCalculators[i++] = experimentFolder.getMetricCalculator(metric);
                }

                for (int id : arguments.getRuns()) {
                    experiment.evaluate(id, metricCalculators);
                }
                break;
            default:
                throw new RuntimeException("Unknown mode: " + arguments.getMode());
        }
    }
}
