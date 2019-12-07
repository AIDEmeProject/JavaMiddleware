package application;

import io.CSVParser;
import explore.ExperimentConfiguration;
import com.google.gson.Gson;

import data.DataPoint;
import data.IndexedDataset;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;



public class ChooseSessionOptionServel extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        Map<String, String[]> postData = req.getParameterMap();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");

        String clientJson = req.getParameter("configuration");


        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);
        ExperimentConfiguration.TsmConfiguration tsmConf = configuration.getTsmConfiguration();

        Gson gson = new Gson();

        CSVParser parser = new CSVParser();

        IndexedDataset dataset = parser.buildIndexedDataset(sessionPath + "/data.csv", postData);


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




        resp.getWriter().println(gson.toJson(manager.runInitialSampling(nInitialPoints)));

        this.getServletContext().setAttribute("experimentManager", manager);

    }


}


