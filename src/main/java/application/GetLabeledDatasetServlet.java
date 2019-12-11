package application;

import application.data.CsvDatasetWriter;
import application.data.LabeledPointsDTO;
import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;



class LabeledDatasetBuilder{


    public  ArrayList<LabeledPoint> getLabeledPoints(ExplorationManager manager){
        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();

        return labeledPoints;
    }

    public void saveLabeledPointsAsCSV(ExplorationManager manager, String filePath){

    }
}

public class GetLabeledDatasetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        //return file

        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");
        String filePath =  sessionPath + "/labeled_dataset.csv";


        labeledPoints.removeIf(s -> s.getLabel().asSign() != 1);
        CsvDatasetWriter writer = new CsvDatasetWriter();
        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);


        //Sending the file
        String contentType = "application/octet-stream";
        // Find this file id in database to get file name, and file type

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType(contentType);

        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename=labeled_dataset.csv");

        File my_file = new File(filePath);

        // This should send the file to browser
        OutputStream out = resp.getOutputStream();
        FileInputStream in = new FileInputStream(my_file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();

    }
}


