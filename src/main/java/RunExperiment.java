import explore.Experiment;
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
                for (int id : arguments.getRuns()) {
                    for (String calculatorId : arguments.getMetrics()) {
                        experiment.evaluate(id, calculatorId);
                    }
                }
                break;
            default:
                throw new RuntimeException("Unknown mode: " + arguments.getMode());
        }
    }
}
