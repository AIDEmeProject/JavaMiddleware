package application;

import application.data.CsvDatasetReader;

import explore.Experiment;

import io.FolderManager;
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
import java.io.IOException;

import java.util.List;


public class NewSessionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //read Dataset and build LabeledDataset labeledDataset, Ranker ranker, BufferedWriter labeledPointsWriter, BufferedWriter metricsWriter


        resp.setContentType("application/json");

        String sessionId = "2323232323232323";
        String sessionPath = "./session/" + sessionId;
        boolean success = (new File(sessionPath)).mkdirs();

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
            String fieldName = uploadedFile.getFieldName();
            String fileName = uploadedFile.getName();
            String contentType = uploadedFile.getContentType();
            boolean isInMemory = uploadedFile.isInMemory();
            long sizeInBytes = uploadedFile.getSize();

            File file = new File( sessionPath + "/data.csv");

            uploadedFile.write(file);

            CsvDatasetReader csvDatasetReader = new CsvDatasetReader();

            String columnNames = csvDatasetReader.getColumnNames(file);

            resp.setStatus(HttpStatus.OK_200);

            resp.getWriter().println(columnNames);


        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


