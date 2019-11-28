package application;

import explore.ExperimentConfiguration;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import data.DataPoint;
import data.IndexedDataset;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


class ValueParser{
    protected int nColumns;

    protected ArrayList<HashMap<String, Integer>> columns;

    public ValueParser(int nColumns){

        this.nColumns = nColumns;
        this.columns = new ArrayList<HashMap<String, Integer>>();

        for (int i = 0; i<nColumns; i++){
            columns.add(new HashMap<String, Integer>());
        }
    }

    public double parseValue(String data, int iColumn){
        double val;
        try{
            val = Double.parseDouble(data);
        }
        catch (NumberFormatException e){
            val = this.getCategory(data, iColumn);
        }
        return val;
    }

    protected int getCategory(String data, int iColumn){

        HashMap<String, Integer> cat = this.columns.get(iColumn);
        int newCategory = cat.size();

        if (! cat.containsKey(data)){
            cat.put(data, newCategory);
            return newCategory;
        }
        return cat.get(data);
    }

}


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


        columnIds.sort(Comparator.comparingInt((Integer n) -> n));



        ArrayList<Double> rowValues = new ArrayList();

        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        Integer rowNumber = 0;
        String [] nextLine;

        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        reader.readNext();

        ValueParser parser = new ValueParser((columnIds.size()));
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line

            rowValues.removeAll(rowValues);
            int i = 0;
            for (Integer id : columnIds){

                Double value = parser.parseValue(nextLine[id], i);

                rowValues.add(value);
                i++;
            }

            double[] doubleRowValues = this.doubleConversion(rowValues);
            DataPoint dataPoint = new DataPoint(rowNumber, doubleRowValues);

            dataPoints.add(dataPoint);

            builder.add(dataPoint);
            rowNumber++;
        }


        String clientJson = req.getParameter("configuration");
        System.out.println(clientJson);

        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        ExperimentConfiguration.TsmConfiguration tsmConf = configuration.getTsmConfiguration();
        System.out.println(configuration.hasMultiTSM());
        System.out.println(tsmConf.getSearchUnknownRegionProbability());
        System.out.print(configuration.getUseFakePoint());

        Gson gson = new Gson();

        IndexedDataset dataset = builder.build();


        double C = 1000;
        Kernel kernel = new GaussianKernel();
        Learner learner = new SvmLearner(C, kernel);
        ExplorationManager manager = new ExplorationManager(dataset, configuration, learner);


        int nInitialPoints = 3;


        //manager.generateGridOfFakePoints();

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


