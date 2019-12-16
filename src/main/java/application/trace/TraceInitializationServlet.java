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

package application.trace;

import application.ExplorationManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.IndexedDataset;
import config.ExperimentConfiguration;
import io.CSVParser;
import io.json.JsonConverter;
import machinelearning.active.LearnerFactory;
import machinelearning.classifier.Learner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;


class ColumnDTO{

   ArrayList<Integer> columnIds;
}


class FullTraceComputing{

}

public class TraceInitializationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Gson json = new Gson();

        String clientJson = req.getParameter("configuration");
        String encodedDatasetName = req.getParameter("encodedDatasetName");
        String strColumnIds = req.getParameter("columnIds");
        String algorithmName = req.getParameter("algorithm");

        System.out.println("");
        System.out.println("---New trace session---");
        System.out.println("");
        System.out.println(algorithmName);

        System.out.println("");
        System.out.println(encodedDatasetName);
        System.out.println("");


        //TODO: get id column index from req. '0' works for the cars dataset.
        int keyColumnId = 0;

        ArrayList<Integer> columnIds = json.fromJson(
                                            strColumnIds,
                                            new TypeToken< ArrayList<Integer>>(){}.getType());



        System.out.println("Load csv " + encodedDatasetName);
        System.out.println("It should be at the root of the project");

        CSVParser parser = new CSVParser();
        IndexedDataset carDataset = parser.buildIndexedDataset(encodedDatasetName, columnIds, keyColumnId);


        System.out.println("Dataset. Col and rows");
        System.out.println(carDataset.getData().cols());
        System.out.println(carDataset.length());
        System.out.println();
        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        System.out.println("configuration");
        System.out.println(json.toJson(configuration));
        System.out.println("");

        System.out.println("Is TSM enabled");
        System.out.println(configuration.hasMultiTSM());
        System.out.println("");


        Learner learner = (new LearnerFactory()).buildLearner(algorithmName, configuration);
        //System.out.println(learner.getClass().toString());
        /*
        Learner learner;
        if  (configuration.getActiveLearner() instanceof SimpleMargin){

            System.out.println("SVM");
            double C = 1000;
            Kernel kernel = new GaussianKernel();
            learner = new SvmLearner(C, kernel);

        }
        else{
            System.out.println("Version space");
            learner = ((UncertaintySampler) configuration.getActiveLearner()).getLearner();
        }
        */

        //double C = 1000;
        //Kernel kernel = new GaussianKernel();
        //learner = new SvmLearner(C, kernel);

        System.out.println("Columns of data");
        System.out.println(carDataset.getData().cols());
        ExplorationManager manager = new ExplorationManager(carDataset, configuration, learner);

        IndexedDataset fakePoints = manager.getRawDataset();

        this.getServletContext().setAttribute("experimentManager", manager);


        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(fakePoints.toList()));

    }
}


