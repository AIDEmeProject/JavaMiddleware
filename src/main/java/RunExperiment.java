import explore.Experiment;
import explore.statistics.StatisticsCalculator;
import io.CommandLineArguments;
import io.FolderManager;

public class RunExperiment {
    private static FolderManager experimentFolder;
    private static Experiment experiment;

    private static Experiment getExperiment() {
        if (experiment == null) {
            experiment = new Experiment(experimentFolder);
        }
        return experiment;
    }

    public static void main(String[] args) {
        CommandLineArguments arguments = CommandLineArguments.parseCommandLineArgs(args);
        System.out.println(arguments);

        experimentFolder = new FolderManager(arguments.getExperimentDirectory());

        if (arguments.getModes().contains("NEW")) {
            for (int i = 0; i < arguments.getNumRuns(); i++) {
                getExperiment().run(arguments.getBudget());
            }
        }

        if (arguments.getModes().contains("RESUME")) {
            for (int id : arguments.getRuns()) {
                getExperiment().resume(id, arguments.getBudget());
            }
        }

        if (arguments.getModes().contains("EVAL")) {
            for (int id : arguments.getRuns()) {
                for (String calculatorId : arguments.getMetrics()) {
                    getExperiment().evaluate(id, calculatorId);
                }
            }
        }

        if (arguments.getModes().contains("AVERAGE")) {
            for (String calculatorId : arguments.getMetrics()) {
                StatisticsCalculator.averageRunFiles(experimentFolder.getAllEvalFiles(calculatorId), experimentFolder.getAverageFile(calculatorId));
            }
        }
    }
}
