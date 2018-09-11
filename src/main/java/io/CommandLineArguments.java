package io;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;

import java.util.ArrayList;
import java.util.List;

public class CommandLineArguments {
    @Parameter(names = "--experiment_dir", required = true)
    private String experimentDirectory;

    @Parameter(names = "--mode", required = true)
    private String mode;

    @Parameter(names = "--budget", validateWith = PositiveInteger.class)
    private int budget = 0;

    @Parameter(names = "--num_runs", validateWith = PositiveInteger.class)
    private int numRuns = 0;

    @Parameter(names = "--runs", variableArity = true)
    private List<Integer> runs = new ArrayList<>();

    @Parameter(names = "--metrics", variableArity = true)
    private List<String> metrics = new ArrayList<>();

    private CommandLineArguments() {

    }

    public String getExperimentDirectory() {
        return experimentDirectory;
    }

    public String getMode() {
        return mode;
    }

    public int getBudget() {
        return budget;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public List<Integer> getRuns() {
        return runs;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public static CommandLineArguments parseCommandLineArgs(String[] args) {
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        return arguments;
    }

    @Override
    public String toString() {
        return "CommandLineArguments{" +
                "experimentDirectory='" + experimentDirectory + '\'' +
                ", mode='" + mode + '\'' +
                ", budget=" + budget +
                ", numRuns=" + numRuns +
                ", runs=" + runs +
                ", metrics=" + metrics +
                '}';
    }
}

