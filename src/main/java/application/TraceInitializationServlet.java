package application;

import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;
import explore.ExperimentConfiguration;
import io.CSVParser;
import io.json.JsonConverter;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Map;




public class TraceInitializationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        Gson json = new Gson();

        CSVParser parser = new CSVParser();
        Map<String, String[]> postData = req.getParameterMap();

        IndexedDataset carDataset = parser.buildIndexedDataset("./car_raw.csv", postData);

        String clientJson = req.getParameter("configuration");
        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        double C = 1000;
        Kernel kernel = new GaussianKernel();
        Learner learner = new SvmLearner(C, kernel);
        ExplorationManager manager = new ExplorationManager(carDataset, configuration, learner);


        this.getServletContext().setAttribute("experimentManager", manager);

    }
}


