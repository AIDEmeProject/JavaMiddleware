package application;

import com.google.gson.Gson;
import data.LabeledPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class getTSMPredictionOverFakePointGridServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        ArrayList<LabeledPoint> labeledPoints = manager.computeTSMPredictionOverRealDataset();

        Gson gson = new Gson();

        String strLabeledPoints = gson.toJson(labeledPoints);

        resp.getWriter().println(strLabeledPoints);
    }
}
