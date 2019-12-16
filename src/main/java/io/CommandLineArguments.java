/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package io;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;

import java.util.ArrayList;
import java.util.List;

public class CommandLineArguments {
    @Parameter(names = "--experiment_dir", required = true)
    private String experimentDirectory;

    @Parameter(names = "--mode", required = true, variableArity = true)
    private List<String> modes;

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

    public List<String> getModes() {
        return modes;
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
                ", mode='" + modes + '\'' +
                ", budget=" + budget +
                ", numRuns=" + numRuns +
                ", runs=" + runs +
                ", metrics=" + metrics +
                '}';
    }
}

