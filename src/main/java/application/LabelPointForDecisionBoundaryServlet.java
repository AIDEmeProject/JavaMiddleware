package application;

import application.trace.ModelProjectionComputer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LabelPointForDecisionBoundaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ExplorationManager manager = (ExplorationManager) this.getServletContext().getAttribute("experimentManager");

        String filePath = "./labeled_points_java.csv";

        ModelProjectionComputer c = new ModelProjectionComputer();

        String jsonEmbedding = c.getEmbbeddingAsJson(filePath, manager);
        resp.getWriter().println(jsonEmbedding);
    }
}


