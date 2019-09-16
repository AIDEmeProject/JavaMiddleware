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


public class FakePointInitialSampling extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        //UserExperimentManager manager = (UserExperimentManager) this.getServletContext().getAttribute("experimentManager");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String jsonFakePoint = req.getParameter("fakePoint");
        System.out.println(jsonFakePoint);
        Gson json = new Gson();
        //stuff to get the data from the POST request

        LabeledPointsDTO converter = new LabeledPointsDTO();
        LabeledPoint labeledPoint = converter.getFakePoint(jsonFakePoint);


        manager.addLabeledPointToDataset(labeledPoint);

        resp.getWriter().println("{'msg': 'ok'}");
    }
}


