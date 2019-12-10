package application;

import application.filtering.Filter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.DataPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/*
class FilterDTO{
    int colId;

    String columnName;
}


class NumericalFilterDTO extends FilterDTO{


    double min;
    double max;

}

class CategoricalFilterDTO extends FilterDTO{

    String[] chosenCategoriesRawValues;

}


*/


public class GetPointToLabelFromFilteringServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        String jsonFilters = req.getParameter("filters");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        Gson gson = new Gson();

        ArrayList<Filter> filters = gson.fromJson(jsonFilters, new TypeToken<ArrayList<Filter>>(){}.getType());

        ArrayList<DataPoint> specificPoints = manager.getPointsFromFilters(filters);


        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(specificPoints));

    }


}


