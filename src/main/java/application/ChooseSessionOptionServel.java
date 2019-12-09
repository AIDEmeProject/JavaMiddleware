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


        ExperimentConfiguration configuration = JsonConverter.deserialize(jsonConfiguration, ExperimentConfiguration.class);
        TsmConfiguration tsmConf = configuration.getTsmConfiguration();



        CSVParser parser = new CSVParser();

        IndexedDataset dataset = parser.buildIndexedDataset(sessionPath + "/data.csv", columnIds);

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


