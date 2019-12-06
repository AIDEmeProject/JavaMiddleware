package application;

import application.data.CsvDatasetWriter;
import com.google.gson.Gson;
import data.IndexedDataset;
import data.LabeledDataset;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;




public class LabelPointForDecisionBoundaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String filePath = "./labeled_points_java.csv";

        ModelProjectionComputer c = new ModelProjectionComputer();

        String jsonEmbedding = c.getEmbbeddingAsJson(filePath, manager);
        resp.getWriter().println(jsonEmbedding);
    }
}


