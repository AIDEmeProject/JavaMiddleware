import explore.Experiment;
import explore.statistics.StatisticsCalculator;
import io.CommandLineArguments;
import io.FolderManager;

public class RunExperiment {

    public static void main(String[] args) {
        CommandLineArguments arguments = CommandLineArguments.parseCommandLineArgs(args);
        System.out.println(arguments);

        FolderManager experimentFolder = new FolderManager(arguments.getExperimentDirectory());
        Experiment experiment = new Experiment(experimentFolder);

        if (arguments.getModes().contains("NEW")) {
            for (int i = 0; i < arguments.getNumRuns(); i++) {
                experiment.run(arguments.getBudget());
            }
        }

        if (arguments.getModes().contains("RESUME")) {
            for (int id : arguments.getRuns()) {
                experiment.resume(id, arguments.getBudget());
            }
        }

        if (arguments.getModes().contains("EVAL")) {
            for (int id : arguments.getRuns()) {
                for (String calculatorId : arguments.getMetrics()) {
                    experiment.evaluate(id, calculatorId);
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
