package application.trace;

import application.ExplorationManager;

import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedLabel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


class TraceResultsComputer{
/*

    public void pointsWereLabeled(ExplorationManager manager, ArrayList<LabelDTO> labeledPointData) throws IOException{


        for (LabelDTO point:labeledPointData
             ) {

            LabeledPoint labeledPoint = this.getLabeledPoint(manager, point.id, point.label);
            ArrayList<LabeledPoint> points = new ArrayList<>();
            points.add(labeledPoint);
            manager.getNextPointsToLabel(points);
        }
    }

    public void pointsWereLabeled(ExplorationManager manager, ArrayList<TSMLabelDTO> labeledPointData) throws IOException{


        for (TSMLabelDTO point:labeledPointData
        ) {

            LabeledPoint labeledPoint = this.getLabeledPoint(manager, point.id, point.labels);
            ArrayList<LabeledPoint> points = new ArrayList<>();
            points.add(labeledPoint);
            manager.getNextPointsToLabel(points);
        }
    }

*/

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
            dto.TSMPredictionsOverGrid = manager.getTSMPredictionOnRealData();
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

class TSMLabelDTO{

    long id;

    int[] labels;
}

class TSMLabelsDTO{
    public ArrayList<TSMLabelDTO> data;
}

class LabelsDto{
    public ArrayList<LabelDTO> data;
}

public class TraceComputationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("");
        System.out.println("--Step starting--");
        System.out.println("");
        Gson json = new Gson();
        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String strLabelData = req.getParameter("labeledPoints");
        System.out.println(strLabelData);

        LabeledPointsDTO dtoManager = new LabeledPointsDTO();
        ArrayList<LabeledPoint> labeledPoints;

        if (manager.useTSM() || manager.useFactorizationInformation()){
             labeledPoints = (ArrayList<LabeledPoint>) dtoManager.getTSMLabeledPoints(strLabelData);
        }
        else{
            labeledPoints = (ArrayList<LabeledPoint>) dtoManager.getLabeledPoints(strLabelData);
        }


        TraceResultsComputer traceResultComputer = new TraceResultsComputer();
        manager.getNextPointsToLabel(labeledPoints);
        TraceResultDTO result = traceResultComputer.computeTraceResults(manager);


        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(result));

        System.out.println("---Step finished---");
    }
}