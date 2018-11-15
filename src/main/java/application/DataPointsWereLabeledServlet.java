package application;

import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import explore.Explore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataPointsWereLabeledServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        UserExperimentManager manager = (UserExperimentManager) this.getServletContext().getAttribute("experimentManager");

        String jsonLabeledPoints = req.getParameter("labeledPoints");
        Gson json = new Gson();
        //stuff to get the data from the POST request

        LabeledPointsDTO converter = new LabeledPointsDTO();

        ArrayList<LabeledPoint> labeledPoints = (ArrayList<LabeledPoint>) converter.getLabeledPoints(jsonLabeledPoints);

        ArrayList<DataPoint> nextPointsToLabel = manager.nextIteration(labeledPoints);

        resp.getWriter().println(json.toJson(nextPointsToLabel));
    }
}


