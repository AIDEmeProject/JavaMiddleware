package application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.DataPoint;
import data.IndexedDataset;
import data.LabeledPoint;
import explore.ExperimentConfiguration;
import explore.user.GuiUserLabel;
import explore.user.UserLabel;
import io.CSVParser;
import io.json.JsonConverter;
import machinelearning.classifier.Label;
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

    public void pointsWereLabeled(ExplorationManager manager, ArrayList<LabelDTO> labeledPointData) throws IOException{


        for (LabelDTO point:labeledPointData
             ) {

            LabeledPoint labeledPoint = this.getLabeledPoint(manager, point.id, point.label);
            ArrayList<LabeledPoint> points = new ArrayList<>();
            points.add(labeledPoint);
            manager.getNextPointsToLabel(points);
        }
    }

    protected LabeledPoint getLabeledPoint(ExplorationManager manager, long index, int intLabel){


        DataPoint dataPoint = new DataPoint(index, manager.getPoint(index).getData());

        Label label = Label.fromSign((double) intLabel);
        LabeledPoint lblPoint = new LabeledPoint(dataPoint, label);
        return lblPoint;

        //DataPoint point = manager.getPoint(index);
        //serLabel userLabel = new GuiUserLabel(label);

        //LabeledPoint labeledPoint = new LabeledPoint(point, userLabel);

        //return labeledPoint;
    }

    TraceResultDTO computeTraceResults(ExplorationManager manager) throws IOException{

        TraceResultDTO dto = new TraceResultDTO();

        ModelProjectionComputer computer = new ModelProjectionComputer();

        dto.labeledPointsOverGrid = manager.computeModelPredictionsOverRealDataset();

        String filePath = "./labeled_points_java.csv";
        String json = computer.getEmbbeddingAsJson(filePath, manager);
        dto.jsonProjectionPredictions = json;

        if (manager.useTSM()){
            dto.TSMPredictionsOverGrid = manager.computeTSMPredictionOverRealDataset();
        }

        return dto;
    }


}

class TraceResultDTO{

    protected ArrayList<LabeledPoint> labeledPointsOverGrid;

    protected ArrayList<LabeledPoint> TSMPredictionsOverGrid;

    protected String jsonProjectionPredictions;

}

class QueryTraceLoader{

    public ArrayList<LabeledPoint> getNextLabeledsPoints(){
        return new ArrayList<>();

    }
}


class LabelDTO{

    long id;

    int label;
}

class LabelsDto{
    public ArrayList<LabelDTO> data;
}

public class TraceComputationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Gson json = new Gson();
        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String strLabelData = req.getParameter("labelData");
        System.out.println(strLabelData);
        ArrayList<LabelDTO> labeledPointsData = json.fromJson(strLabelData, LabelsDto.class).data;


        TraceResultsComputer traceResultComputer = new TraceResultsComputer();
        traceResultComputer.pointsWereLabeled(manager, labeledPointsData);
        TraceResultDTO result = traceResultComputer.computeTraceResults(manager);


        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(result));
    }
}