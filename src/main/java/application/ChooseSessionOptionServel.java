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

package application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.ExperimentConfiguration;
import config.TsmConfiguration;
import data.IndexedDataset;
import io.CSVParser;
import io.json.JsonConverter;
import machinelearning.active.learning.SimpleMargin;
import machinelearning.active.learning.UncertaintySampler;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;



public class ChooseSessionOptionServel extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        Gson gson = new Gson();


        Map<String, String[]> postData = req.getParameterMap();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");

        String jsonConfiguration = req.getParameter("configuration");
        String jsonColumnIds = req.getParameter("columnIds");
        ArrayList<Integer> columnIds = gson.fromJson(jsonColumnIds, new TypeToken<ArrayList<Integer>>(){}.getType());
        System.out.println("column ids");
        System.out.println(jsonColumnIds);

        //TODO: get id column index from req. '0' works for the cars dataset.
        int keyColumnId = 0;


        ExperimentConfiguration configuration = JsonConverter.deserialize(jsonConfiguration, ExperimentConfiguration.class);
        TsmConfiguration tsmConf = configuration.getTsmConfiguration();



        CSVParser parser = new CSVParser();

        IndexedDataset dataset = parser.buildIndexedDataset(sessionPath + "/data.csv", columnIds, keyColumnId);

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

        //double C = 1000;
        //Kernel kernel = new GaussianKernel();
        //Learner learner = new SvmLearner(C, kernel);
        ExplorationManager manager = new ExplorationManager(dataset, configuration, learner);

        int nInitialPoints = 3;

        this.getServletContext().setAttribute("experimentManager", manager);

        resp.setContentType("application/json");
        resp.getWriter().println(gson.toJson(manager.runInitialSampling(nInitialPoints)));

    }


}


