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

import application.data.CsvDatasetWriter;
import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;



class LabeledDatasetBuilder{


    public  ArrayList<LabeledPoint> getLabeledPoints(ExplorationManager manager){
        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();

        return labeledPoints;
    }

    public void saveLabeledPointsAsCSV(ExplorationManager manager, String filePath){

    }
}

public class GetLabeledDatasetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        //return file

        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");
        String filePath =  sessionPath + "/labeled_dataset.csv";


        labeledPoints.removeIf(s -> s.getLabel().asSign() != 1);
        CsvDatasetWriter writer = new CsvDatasetWriter();
        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);


        //Sending the file
        String contentType = "application/octet-stream";
        // Find this file id in database to get file name, and file type

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType(contentType);

        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename=labeled_dataset.csv");

        File my_file = new File(filePath);

        // This should send the file to browser
        OutputStream out = resp.getOutputStream();
        FileInputStream in = new FileInputStream(my_file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();

    }
}


