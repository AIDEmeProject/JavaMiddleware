package application;

import application.data.CsvDatasetReader;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import data.DataPoint;
import data.IndexedDataset;
import explore.ExperimentConfiguration;
import io.json.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.*;


public class ChooseSessionOptionServel extends HttpServlet {

    @Override

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        Map<String, String[]> postData = req.getParameterMap();

        String sessionPath = (String) req.getSession().getAttribute("sessionPath");

        sessionPath = (String) this.getServletContext().getAttribute("sessionPath");

        Reader in = new FileReader(sessionPath + "/data.csv");

        CSVReader reader = new CSVReader(in);

        Integer columnId;

        ArrayList<Integer> columnIds = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : postData.entrySet()){

            String strColumnId = String.join(",", entry.getValue());
            columnId = Integer.parseInt(strColumnId);

            columnIds.add(columnId);
        }

        ArrayList<Double> rowValues = new ArrayList();

        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        Integer rowNumber = 0;
        String [] nextLine;

        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line

            rowValues.removeAll(rowValues);
            for (Integer id : columnIds){

                rowValues.add(Double.parseDouble(nextLine[id]));
            }

            double[] doubleRowValues = this.doubleConversion(rowValues);
            DataPoint dataPoint = new DataPoint(rowNumber, doubleRowValues);

            dataPoints.add(dataPoint);

            builder.add(dataPoint);
            rowNumber++;
        }


        String json = "{\n" +
                "   \"activeLearner\": {\n" +
                "       \"learner\": {\n" +
                "           \"name\": \"MajorityVote\",\n" +
                "           \"sampleSize\": 8,\n" +
                "           \"versionSpace\": {\n" +
                "               \"addIntercept\": true,\n" +
                "               \"hitAndRunSampler\": {\n" +
                "                   \"cache\": true,\n" +
                "                   \"rounding\": true,\n" +
                "                   \"selector\": {\n" +
                "                       \"name\": \"WarmUpAndThin\",\n" +
                "                       \"thin\": 10,\n" +
                "                       \"warmUp\": 100\n" +
                "                   }\n" +
                "               },\n" +
                "               \"kernel\": {\n" +
                "                   \"name\": \"gaussian\"\n" +
                "               },\n" +
                "               \"solver\": \"ojalgo\"\n" +
                "           }\n" +
                "       },\n" +
                "       \"name\": \"UncertaintySampler\"\n" +
                "   },\n" +
                "   \"subsampleSize\": 50000,\n" +
                "   \"task\": \"sdss_Q4_0.1%\"\n" +
                "}";

        ExperimentConfiguration configuration = JsonConverter.deserialize(json, ExperimentConfiguration.class);

        IndexedDataset dataset = builder.build();

        UserExperimentManager manager = new UserExperimentManager(configuration, dataset);

        Gson gson = new Gson();

        resp.getWriter().println(gson.toJson(manager.nextIteration(new ArrayList<>())));

        this.getServletContext().setAttribute("experimentManager", manager);

    }


    public double[] doubleConversion(ArrayList<Double> values){

        double[] convertedValues = new double[values.size()];
        int index = 0;
        for(Double value: values){

            convertedValues[index] = value;
            index++;
        }

        return convertedValues;
    }
}


