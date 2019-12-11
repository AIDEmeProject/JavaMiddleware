package application.trace;

import application.ExplorationManager;
import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.LabeledPoint;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class TraceResultsComputer{
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

    TraceResultDTO computeJobsTraceResult(ExplorationManager manager, PredictionReader reader, int iteration) throws IOException {
        // TODO: check logic
        TraceResultDTO dto = new TraceResultDTO();

        List<LabeledPoint> labeledPoints = manager.getLabeledPointFromIteration(reader, iteration);

        dto.labeledPointsOverGrid = labeledPoints;

        String filePath = "./labeled_points_java.csv";
        ModelProjectionComputer computer = new ModelProjectionComputer();
        dto.jsonProjectionPredictions = computer.getEmbeddingAsJson(filePath, labeledPoints);

        return dto;
    }
}


class TraceResultDTO{
    List<LabeledPoint> labeledPointsOverGrid;

    List<LabeledPoint> TSMPredictionsOverGrid;

    String jsonProjectionPredictions;
}

public class TraceComputationServlet extends HttpServlet {

    private PredictionReader predictionReader = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println();
        System.out.println("--Step starting--");
        System.out.println();
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

        manager.getNextPointsToLabel(labeledPoints);

        TraceResultDTO result = getTraceResult(req, manager);

        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(result));

        System.out.println("---Step finished---");
    }

    private TraceResultDTO getTraceResult(HttpServletRequest req, ExplorationManager manager) throws IOException {
        TraceResultsComputer traceResultComputer = new TraceResultsComputer();

        // TODO: add "dataset" parameter to request with the name of the dataset being used in the trace ("jobs" vs "cars")
        if (!"jobs".equals(req.getParameter("dataset"))) {
            return traceResultComputer.computeTraceResults(manager);
        }

        // TODO: add "algorithm" parameter to request with the name of the algorithm being used ("sm", "vs", "dsm", "factvs")
        if (predictionReader == null) {
            String algorithm = req.getParameter("algorithm");
            predictionReader = new PredictionReader(algorithm);
        }

        // TODO: add "iteration" parameter to request with the current iteration (0 = right after initial sampling)
        int iteration = Integer.parseInt(req.getParameter("iteration"));
        return traceResultComputer.computeJobsTraceResult(manager, predictionReader, iteration);
    }
}

