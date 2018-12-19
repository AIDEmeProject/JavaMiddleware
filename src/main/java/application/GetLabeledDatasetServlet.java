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


public class GetLabeledDatasetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        //return file

        ArrayList<LabeledPoint> labeledPoints = manager.labelWholeDataset();

        CsvDatasetWriter writer = new CsvDatasetWriter();

        String sessionPath = (String) this.getServletContext().getAttribute("sessionPath");
        String filePath =  sessionPath + "/dataset.csv";

        writer.savedLabeledPointsAsCsv(labeledPoints, filePath);

        resp.setContentType("application/json");
        //Envoyer le ficher.

        String id = req.getParameter("id");

        String fileName = "test.csv";
        String fileType = "csv";
        // Find this file id in database to get file name, and file type

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType(fileType);

        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename=labeled_dataset.csv");

        // Assume file name is retrieved from database
        // For example D:\\file\\test.pdf

        File my_file = new File(fileName);

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


