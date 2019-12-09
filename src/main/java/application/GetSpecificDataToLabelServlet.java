package application;

import com.google.gson.Gson;
import data.DataPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


public class GetSpecificDataToLabelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        int id = Integer.parseInt(req.getParameter("id"));

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        ArrayList<DataPoint> specificPoints = manager.getPointByRowId(id);

        Gson gson = new Gson();

        resp.getWriter().write(gson.toJson(specificPoints));

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


