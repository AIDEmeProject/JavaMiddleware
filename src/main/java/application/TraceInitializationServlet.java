package application;

import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;
import explore.ExperimentConfiguration;
import io.CSVParser;
import io.json.JsonConverter;
import machinelearning.active.learning.SimpleMargin;
import machinelearning.active.learning.UncertaintySampler;
import machinelearning.classifier.Learner;
import machinelearning.classifier.MajorityVoteLearner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;


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

        String strColumnIds = req.getParameter("dataLoading");
        ColumnDTO dto = json.fromJson(strColumnIds, ColumnDTO.class);
        dto.columnIds.sort(Comparator.comparingInt((Integer n) -> n));

        String encodedDatasetName = req.getParameter("encodedDatasetName");

        CSVParser parser = new CSVParser();
        IndexedDataset carDataset = parser.buildIndexedDataset(encodedDatasetName, dto.columnIds);

        System.out.println(carDataset.length());

        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);
        //configuration.hasMultiTSM(false);

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

        System.out.println("configuration");
        System.out.println(json.toJson(configuration));

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


