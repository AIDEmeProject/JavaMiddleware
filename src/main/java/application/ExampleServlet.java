package application;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import explore.Experiment;

public class ExampleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        Gson gson = new Gson();

        HashMap<Integer, String> test = new HashMap<Integer, String>();
        test.put(2, "test");
        test.put(3, "test2222");
        String json = gson.toJson(test);

        resp.setStatus(HttpStatus.OK_200);

        resp.setContentType("application/json");

        resp.getWriter().println(json);

        System.console().printf("not working \n");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        System.console().printf("hehehe \n");

        super.doPost(req, resp);

        Map params = req.getParameterMap();

        Gson gson = new Gson();
        String json = gson.toJson(params);

        resp.setStatus(HttpStatus.OK_200);
        resp.setContentType("application/json");
        resp.getWriter().println(json);

    }

}


