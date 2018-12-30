package application;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import config.ExperimentConfiguration;
import config.TsmConfiguration;
import data.DataPoint;
import data.IndexedDataset;
import io.json.JsonConverter;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;


public class GetSpecificDataToLabelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        int id = Integer.parseInt(req.getParameter("id"));

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        ArrayList<DataPoint> specificPoints = manager.getPointByRowId(id);

        Gson gson = new Gson();

        resp.getWriter().write(gson.toJson(specificPoints));

    }

    public double[] doubleConversion(ArrayList<Double> values){

        double[] convertedValues = new double[values.size()];
        int index = 0;
        for(Double value: values){

            convertedValues[index] = value;
            index++;
        }

        return convertedValues;
    }
}


