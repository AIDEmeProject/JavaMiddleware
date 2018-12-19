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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GetLabeledDatasetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        //return file

        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();

        CsvDatasetWriter writer = new CsvDatasetWriter();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");
        String filePath =  sessionPath + "/dataset.csv";

        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);

        resp.setContentType("application/json");
        //Envoyer le ficher.

    }
}


