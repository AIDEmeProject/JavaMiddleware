package application;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.Map;


class TraceResultsComputer{

    TraceResultDTO getTraceResults(ExplorationManager manager, LabeledPoint point){

        TraceResultDTO dto = new TraceResultDTO();


        return dto;


    }


    ArrayList<LabeledPoint> getNextLabeledPointFromTrace(){
        ArrayList<LabeledPoint> labeledPoints = new ArrayList<>();

        return labeledPoints;
    }
}

class TraceResultDTO{

    protected ArrayList<LabeledPoint> labeledPointsOverGrid;

    protected ArrayList<LabeledPoint> projectionPredictions;

}

class QueryTraceLoader{

    public ArrayList<LabeledPoint> getNextLabeledsPoints(){
        return new ArrayList<>();

    }
}


public class TraceComputationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        Gson json = new Gson();
        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");
        TraceResultsComputer traceResultComputer = (TraceResultsComputer) this.getServletContext().getAttribute("traceComputer");


        ArrayList<LabeledPoint> labeledPoints = traceResultComputer.getNextLabeledPointFromTrace();
        ArrayList<TraceResultDTO> traceResults = new ArrayList<>();

        for (LabeledPoint point: labeledPoints
             ) {

            TraceResultDTO result = traceResultComputer.getTraceResults(manager, point);
            traceResults.add(result);
        }

        resp.getWriter().println(json.toJson(traceResults));
    }
}