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

import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TSMDataPointsWereLabeledServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        //UserExperimentManager manager = (UserExperimentManager) this.getServletContext().getAttribute("experimentManager");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String jsonLabeledPoints = req.getParameter("labeledPoints");
        Gson json = new Gson();
        //stuff to get the data from the POST request

        LabeledPointsDTO converter = new LabeledPointsDTO();

        ArrayList<LabeledPoint> labeledPoints = (ArrayList<LabeledPoint>) converter.getTSMLabeledPoints(jsonLabeledPoints);

        System.out.println(labeledPoints.size());
        for (LabeledPoint point: labeledPoints
             ) {

            System.out.println(point.getId());
            System.out.println(point.getLabel().isPositive());

        }
        List<DataPoint> nextPointsToLabel = manager.getNextPointsToLabel(labeledPoints);

        resp.getWriter().println(json.toJson(nextPointsToLabel));
    }
}


