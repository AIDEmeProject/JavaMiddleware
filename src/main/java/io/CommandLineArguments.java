package io;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

import java.util.Arrays;

public class CommandLineArguments {
    @Parameter(names = "--experiment_dir", required = true)
    private String experimentDirectory;

    @Parameter(names = "--mode", required = true)
    private String mode;

    @Parameter(names = "--budget", validateWith = PositiveInteger.class)
    private int budget = 0;

    @Parameter(names = "--num_runs", validateWith = PositiveInteger.class)
    private int numRuns = 0;

    @Parameter(names = "--runs", validateValueWith = PositiveArray.class)
    private int[] runs = new int[0];

    @Parameter(names = "--metrics")
    private String[] metrics = new String[0];

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

    public int[] getRuns() {
        return runs;
    }

    public String[] getMetrics() {
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
                ", runs=" + Arrays.toString(runs) +
                ", metrics=" + Arrays.toString(metrics) +
                '}';
    }
}


class PositiveArray implements IValueValidator<int[]> {
    public void validate(String name, int[] values) throws ParameterException {
        for(int value : values) {
            if (value <= 0) {
                throw new ParameterException("Parameter " + name + " must only contain positive values.");
            }
        }
    }
}
