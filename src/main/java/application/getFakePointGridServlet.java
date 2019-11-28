package application;

import com.google.gson.Gson;
import data.IndexedDataset;
import data.LabeledPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;




public class getFakePointGridServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        IndexedDataset fakePoints = manager.getOrGenerateGridOfFakePoints();

        Gson gson = new Gson();

        String strLabeledPoints = gson.toJson(fakePoints.toList());

        resp.getWriter().println(strLabeledPoints);
    }
}
