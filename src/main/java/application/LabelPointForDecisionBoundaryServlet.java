package application;

import application.data.CsvDatasetWriter;
import com.google.gson.Gson;
import data.IndexedDataset;
import data.LabeledDataset;
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
import java.util.Random;


public class LabelPointForDecisionBoundaryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        int nPoint = 200;
        double[] data = new double[3];

        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        int min = 0;
        int max = 60;

        for (int i=0; i< nPoint; i++){

            Random rand = new Random();

            data[0] = (new Random()).nextFloat() * (max - min) + min;
            data[1] = (new Random()).nextFloat() * (max - min) + min;
            data[2] = (new Random()).nextFloat() * (max - min) + min;
            builder.add(i, data);
        }

        IndexedDataset pointsToLabel = builder.build();

        ArrayList<LabeledPoint> labeledPoints = manager.labelPoints(pointsToLabel, false);

        Gson gson = new Gson();

        resp.getWriter().println(gson.toJson(labeledPoints.toArray()));

    }
}


