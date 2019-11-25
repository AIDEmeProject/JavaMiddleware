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


class MyHttpClient{


    public String getModelBoundary() throws IOException {

        HttpGet request = new HttpGet("http://localhost:5000/umap");
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }
}



public class LabelPointForDecisionBoundaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String filePath = "./src/main/model_behavior/labeled_points_java.csv";
        CsvDatasetWriter writer = new CsvDatasetWriter();
        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();
        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);

        MyHttpClient c = new MyHttpClient();
        String jsonEmbedding = c.getModelBoundary();

        resp.getWriter().println(jsonEmbedding);


        /*
        int nPoint = 200;
        double[] data = new double[3];

        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        int min = 0;
        int max = 60;

        for (int i=0; i< nPoint; i++){

            Random rand = new Random();

            data[0] = (new Random()).nextFloat() * (max - min) + min;
            data[1] = (new Random()).nextFloat() * (max - min) + min;
            data[2] = (new Random()).nextFloat() * (max - min) + min;
            builder.add(i, data);
        }

        IndexedDataset pointsToLabel = builder.build();

        ArrayList<LabeledPoint> labeledPoints = manager.labelPoints(pointsToLabel, false);
        */
        //Gson gson = new Gson();

        //resp.getWriter().println(gson.toJson(labeledPoints.toArray()));

    }
}


