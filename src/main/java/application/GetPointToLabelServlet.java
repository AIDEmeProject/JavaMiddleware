package application;

import com.google.gson.Gson;
import data.DataPoint;
import explore.Explore;
import io.FolderManager;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class GetPointToLabelServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        // get dataset

        //Explore explore = (Explore) req.getSession().getAttribute("explorer");

        //explore.runSingleIteration(labeledDataset, ranker, labeledPointsWriter, metricsWriter);


        Gson json = new Gson();

        //DataPoint nextPoint new DataPoint(2, 2);
        //resp.getWriter().println(json.toJson(nextPoint));


    }

}


