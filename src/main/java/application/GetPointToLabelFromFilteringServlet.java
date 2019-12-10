package application;

import application.filtering.DatabaseFiltering;
import application.filtering.Filter;
import com.google.gson.Gson;
import config.DatasetConfiguration;
import data.DataPoint;
import io.json.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public class GetPointToLabelFromFilteringServlet extends HttpServlet {

    private DatabaseFiltering filtering = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (filtering == null) {
            initialize();
        }

        String jsonFilters = req.getParameter("filters");

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        // parse filters
        Filter[] filters = JsonConverter.deserialize(jsonFilters, Filter[].class);

        // run query on DB
        Set<Long> indexes = filtering.getElementsMatchingFilter(filters);
        List<DataPoint> specificPoints = manager.getPointsFromFilters(indexes);

        // send results
        resp.setContentType("application/json");
        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(specificPoints));

    }

    private void initialize() {
        DatasetConfiguration configuration = new DatasetConfiguration("cars_raw");
        this.filtering = configuration.buildFilter();
    }
}


