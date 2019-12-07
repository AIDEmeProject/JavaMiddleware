package application.trace;

import application.ExplorationManager;
import application.data.CsvDatasetWriter;
import data.LabeledPoint;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class ModelProjectionComputer{

    public String getModelBoundary() throws IOException {

        HttpGet request = new HttpGet("http://localhost:5000/umap");
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

    public String getEmbbeddingAsJson(String filePath, ExplorationManager manager) throws IOException{

        CsvDatasetWriter writer = new CsvDatasetWriter();
        ArrayList<LabeledPoint> labeledPoints = manager.computeModelPredictionForProjection();
        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);

        String jsonEmbedding = this.getModelBoundary();
        return jsonEmbedding;

    }
}

