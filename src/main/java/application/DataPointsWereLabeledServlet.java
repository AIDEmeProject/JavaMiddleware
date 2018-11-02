package application;

import com.google.gson.Gson;
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


public class DataPointsWereLabeledServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        UserExperimentManager manager = (UserExperimentManager) this.getServletContext().getAttribute("experimentManager");

        //stuff to get the data from the POST request
        ArrayList<LabeledPoint> labeledPoints = new ArrayList<>();

        ArrayList<DataPoint> nextPointsToLabel = manager.nextIteration(labeledPoints);

        Gson json = new Gson();


        System.out.println(json.toJson(req.getParameterMap()));


        resp.getWriter().println(json.toJson(nextPointsToLabel));
    }
}


