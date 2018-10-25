package application;

import com.google.gson.Gson;
import data.DataPoint;
import data.LabeledDataset;
import explore.Explore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class GetLabeledDatasetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");


        Gson json = new Gson();


        //LabeledDataset labeledDataSet = new LabeledDataset();

        //resp.getWriter().println(json.toJson(labeledDataSet));

    }

}


