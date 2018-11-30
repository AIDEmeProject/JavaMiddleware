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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        //UserExperimentManager manager = (UserExperimentManager) this.getServletContext().getAttribute("experimentManager");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String jsonLabeledPoints = req.getParameter("labeledPoints");


        manager.getModelVisualizationData();




        //resp.getWriter().println(json.toJson(nextPointsToLabel));
    }
}


