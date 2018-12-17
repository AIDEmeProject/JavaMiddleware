package application;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import data.DataPoint;
import data.IndexedDataset;
import config.ExperimentConfiguration;
import io.json.JsonConverter;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;


public class ChooseSessionOptionServel extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        Map<String, String[]> postData = req.getParameterMap();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");

        Reader in = new FileReader(sessionPath + "/data.csv");

        CSVReader reader = new CSVReader(in);

        Integer columnId;

        ArrayList<Integer> columnIds = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : postData.entrySet()){

            if (entry.getKey().indexOf("column") != -1){

                String strColumnId = String.join(",", entry.getValue());
                columnId = Integer.parseInt(strColumnId);

                columnIds.add(columnId);
            }
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
                "           \"name\": \"MajorityVote\",\n" + // MajorityVote | SVM |
                "           \"sampleSize\": 8,\n" + // Only for MajorityVote : >= 1
                "           \"versionSpace\": {\n" +
                "               \"addIntercept\": true,\n" +
                "               \"hitAndRunSampler\": {\n" +
                "                   \"cache\": true,\n" +
                "                   \"rounding\": true,\n" +
                "                   \"selector\": {\n" +
                "                       \"name\": \"WarmUpAndThin\",\n" + // Only For Majority Vote
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

        String json2 = "{\n" +
                "   \"activeLearner\": {\n" +
                "       \"name\": \"SimpleMargin\",\n" +
                "       \"svmLearner\": {\n" +
                "           \"C\": 1024,\n" +
                "           \"kernel\": {\n" +
                "               \"name\": \"gaussian\"\n" +
                "           },\n" +
                "           \"name\": \"SVM\"\n" +
                "       }\n" +
                "   },\n" +
                "   \"multiTSM\": {\n" + // pas de champ si n'est pas activé
                "       \"searchUnknownRegionProbability\": 0.5\n" +
                "       \"featureGroups\": [" +
                "                ['age'], ['sex']" +
                "           ]" +
                "       \"columns: ['age', 'sex']    " +

                "   },\n" +
                "   \"subsampleSize\": 5000,\n" +
                "   \"task\": \"sdss_Q1_0.1%\"\n" +
                "}";

        String clientJson = req.getParameter("configuration");

        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        IndexedDataset dataset = builder.build();

        //UserExperimentManager manager = new UserExperimentManager(configuration, dataset);
        double C = 1000;
        Kernel kernel = new GaussianKernel();
        Learner learner = new SvmLearner(C, kernel);
        ExplorationManager manager = new ExplorationManager(dataset, configuration, learner);
        Gson gson = new Gson();

        int nInitialPoints = 3;
        resp.getWriter().println(gson.toJson(manager.runInitialSampling(nInitialPoints)));

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


