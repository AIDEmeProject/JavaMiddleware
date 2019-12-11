package application.trace;

import application.ExplorationManager;
import com.google.gson.Gson;
import config.ExperimentConfiguration;
import data.IndexedDataset;
import io.CSVParser;
import io.json.JsonConverter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;


public class FullTraceComputationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson json = new Gson();

        String clientJson = req.getParameter("configuration");
        String encodedDatasetName = req.getParameter("encodedDatasetName");
        String strColumnIds = req.getParameter("dataLoading");
        String algorithmName = req.getParameter("algorithm");
        System.out.println(algorithmName);

        ColumnDTO dto = json.fromJson(strColumnIds, ColumnDTO.class);
        dto.columnIds.sort(Comparator.comparingInt((Integer n) -> n));

        //TODO: get id column index from req. '0' works for the cars dataset.
        int keyColumnId = 0;

        CSVParser parser = new CSVParser();
        IndexedDataset carDataset = parser.buildIndexedDataset(encodedDatasetName, dto.columnIds, keyColumnId);

        System.out.println(carDataset.length());

        ExperimentConfiguration configuration = JsonConverter.deserialize(clientJson, ExperimentConfiguration.class);

        System.out.println("configuration");
        System.out.println(json.toJson(configuration));

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String strLabelData = req.getParameter("labelData");
        System.out.println(strLabelData);

        TraceResultsComputer traceResultComputer = new TraceResultsComputer();
        TraceResultDTO result = traceResultComputer.computeTraceResults(manager);

        resp.setContentType("application/json");
        resp.getWriter().println(json.toJson(result));
    }
}