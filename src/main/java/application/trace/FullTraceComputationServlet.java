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
import config.ExperimentConfiguration;
import data.IndexedDataset;
import io.CSVParser;
import io.json.JsonConverter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;


public class FullTraceComputationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson json = new Gson();

        String clientJson = req.getParameter("configuration");
        String encodedDatasetName = req.getParameter("encodedDatasetName");
        String strColumnIds = req.getParameter("dataLoading");
        String algorithmName = req.getParameter("algorithm");
        System.out.println(algorithmName);

        ColumnDTO dto = json.fromJson(strColumnIds, ColumnDTO.class);
        dto.columnIds.sort(Comparator.comparingInt((Integer n) -> n));

        //TODO: get id column index from req. '0' works for the cars dataset.
        int keyColumnId = 0;

        CSVParser parser = new CSVParser();
        IndexedDataset carDataset = parser.buildIndexedDataset(encodedDatasetName, dto.columnIds, keyColumnId);

        System.out.println(carDataset.length());

        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        System.out.println("configuration");
        System.out.println(json.toJson(configuration));

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String strLabelData = req.getParameter("labelData");
        System.out.println(strLabelData);

        TraceResultsComputer traceResultComputer = new TraceResultsComputer();
        TraceResultDTO result = traceResultComputer.computeTraceResults(manager);

        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(result));
    }
}