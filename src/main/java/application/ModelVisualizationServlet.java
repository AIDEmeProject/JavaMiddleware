package application;

import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ModelVisualizationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");


        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        Gson gson = new Gson();

        ArrayList<LabeledPoint> predictions = manager.labelWholeDataset(4);

        Double TSMBound = manager.getTSMBound();

        VisualizationDataDTO dto = new VisualizationDataDTO(predictions, TSMBound);
        resp.getWriter().println(gson.toJson(dto));
    }
}


class VisualizationDataDTO{

    ArrayList<LabeledPoint> predictions;

    Double TSMBound;

    public VisualizationDataDTO(ArrayList<LabeledPoint> predictions, Double TSMBound) {
        this.predictions = predictions;
        this.TSMBound = TSMBound;
    }
}

