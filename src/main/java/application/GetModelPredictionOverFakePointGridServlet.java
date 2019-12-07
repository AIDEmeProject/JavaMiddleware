package application;

import application.data.CsvDatasetWriter;
import com.google.gson.Gson;
import data.IndexedDataset;
import data.LabeledPoint;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GetModelPredictionOverFakePointGridServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        ArrayList<LabeledPoint> labeledPoints = manager.computeModelPredictionsOverFakeGridPoints();

        Gson gson = new Gson();

        String strLabeledPoints = gson.toJson(labeledPoints);

        resp.getWriter().println(strLabeledPoints);
    }
}