package application;

import application.data.CsvDatasetReader;
import com.google.gson.Gson;
import data.DataPoint;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChooseSessionOptionServel extends HttpServlet {

    @Override

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        resp.setContentType("application/json");


        Map<String, String[]> postData = req.getParameterMap();
        System.out.println(postData.toString());


        for (Map.Entry<String, String[]> entry : postData.entrySet()){

            System.out.println(entry.getValue());
        }

        Gson json = new Gson();



        resp.getWriter().println(json.toJson(req.getParameterMap()));

       /* List<DataPoint> dataPoints = new ArrayList<>();


        Reader in = new FileReader("path/to/file.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        for (CSVRecord record : records) {



            String columnOne = record.get(0);
            String columnTwo = record.get(1);
        }*/


        // Load Dataset with good columns
        // Start all the stuff.

        //save to session

        //redirect to labeling point

    }
}


