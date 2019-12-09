package application.trace;

import application.ExplorationManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.IndexedDataset;
import config.ExperimentConfiguration;
import io.CSVParser;
import io.json.JsonConverter;
import machinelearning.active.LearnerFactory;
import machinelearning.classifier.Learner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;


class ColumnDTO{

   ArrayList<Integer> columnIds;
}


class FullTraceComputing{

}

public class TraceInitializationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Gson json = new Gson();

        String clientJson = req.getParameter("configuration");
        String encodedDatasetName = req.getParameter("encodedDatasetName");
        String strColumnIds = req.getParameter("columnIds");
        String algorithmName = req.getParameter("algorithm");

        System.out.println("");
        System.out.println("---New trace session---");
        System.out.println("");
        System.out.println(algorithmName);

        System.out.println("");
        System.out.println(encodedDatasetName);
        System.out.println("");




        ArrayList<Integer> columnIds = json.fromJson(
                                            strColumnIds,
                                            new TypeToken< ArrayList<Integer>>(){}.getType());

        columnIds.sort(Comparator.comparingInt((Integer n) -> n));


        System.out.println("Load csv " + encodedDatasetName);
        System.out.println("It should be at the root of the project");

        CSVParser parser = new CSVParser();
        IndexedDataset carDataset = parser.buildIndexedDataset(encodedDatasetName, columnIds);


        System.out.println("Dataset. Col and rows");
        System.out.println(carDataset.getData().cols());
        System.out.println(carDataset.length());
        System.out.println();
        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        System.out.println("configuration");
        System.out.println(json.toJson(configuration));
        System.out.println("");

        System.out.println("Is TSM enabled");
        System.out.println(configuration.hasMultiTSM());
        System.out.println("");


        Learner learner = (new LearnerFactory()).buildLearner(algorithmName, configuration);
        //System.out.println(learner.getClass().toString());
        /*
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
        */

        //double C = 1000;
        //Kernel kernel = new GaussianKernel();
        //learner = new SvmLearner(C, kernel);

        System.out.println("Columns of data");
        System.out.println(carDataset.getData().cols());
        ExplorationManager manager = new ExplorationManager(carDataset, configuration, learner);

        IndexedDataset fakePoints = manager.getRawDataset();

        this.getServletContext().setAttribute("experimentManager", manager);


        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(fakePoints.toList()));

    }
}


