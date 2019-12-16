/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
import java.util.List;

public class ModelProjectionComputer{

    public String getModelBoundary() throws IOException {

        HttpGet request = new HttpGet("http://localhost:5000/umap");
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

    public String getEmbbeddingAsJson(String filePath, ExplorationManager manager) throws IOException{
        return getEmbeddingAsJson(filePath, manager.computeModelPredictionForProjection());
    }

    public String getEmbeddingAsJson(String filePath, List<LabeledPoint> labeledPoints) throws IOException{
        CsvDatasetWriter writer = new CsvDatasetWriter();
        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);
        return this.getModelBoundary();
    }
}

