package application;

import application.data.CsvDatasetReader;
import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledDataset;
import explore.Experiment;
import io.FolderManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
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

        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

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
        for (CSVRecord record : records) {

            rowValues.removeAll(rowValues);

            for (Integer id : columnIds){

                rowValues.add(Double.parseDouble(record.get(id)));
            }

            double[] d = rowValues.toArray((new double[0]));
            DataPoint dataPoint = new DataPoint(rowNumber, d);


            dataPoints.add(dataPoint);
            rowNumber++;
        }

        LabeledDataset labeledDataset = new LabeledDataset(dataPoints);

        this.getServletContext().setAttribute("labeledDataset", labeledDataset);

        resp.getWriter().println("point to label is lal");

        // Load Dataset with good columns
        // Start all the stuff.

        //save to session

        //redirect to labeling point

    }
}


