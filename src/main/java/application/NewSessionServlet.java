package application;

import application.data.CsvDatasetReader;

import application.data.CsvDatasetWriter;
import data.LabeledPoint;
import explore.Experiment;

import io.FolderManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;





class CommandLauncher{


    public static void launchCommand(String command){
        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
            Process pr = rt.exec(command);

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            System.out.println("Exited with error code "+exitVal);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}


public class NewSessionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //read Dataset and build LabeledDataset labeledDataset, Ranker ranker, BufferedWriter labeledPointsWriter, BufferedWriter metricsWriter


        String sessionId = "42";
        String sessionPath = "./session/" + sessionId;

        boolean success = (new File(sessionPath)).mkdirs();

        req.getSession().setAttribute("sessionPath", sessionPath);

        this.getServletContext().setAttribute("sessionPath", sessionPath);

        FolderManager folderManager = new FolderManager(sessionPath);

        Experiment experiment = new Experiment(folderManager);
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);

        if (! isMultipart){
            resp.setStatus(HttpStatus.BAD_REQUEST_400);
            resp.getWriter().println("{message: 'error'");
            return;
        }


        Integer maxMemSize = 1024 * 1000 * 100;

        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);

        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File(sessionPath));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);


        try {
            List fileItems = upload.parseRequest(req);
            FileItem uploadedFile = (FileItem) fileItems.get(0);

            /*
            String fieldName = uploadedFile.getFieldName();
            String fileName = uploadedFile.getName();
            String contentType = uploadedFile.getContentType();
            boolean isInMemory = uploadedFile.isInMemory();
            long sizeInBytes = uploadedFile.getSize();
            */
            File file = new File( sessionPath + "/data.csv");

            uploadedFile.write(file);

            CsvDatasetReader csvDatasetReader = new CsvDatasetReader();


            FileItem separatorItem = (FileItem) fileItems.get(1);
            char separator = separatorItem.getString().charAt(0);


            String columnNames = csvDatasetReader.getCsvInfos(file, separator);

            resp.setStatus(HttpStatus.OK_200);

            resp.getWriter().println(columnNames);


        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


