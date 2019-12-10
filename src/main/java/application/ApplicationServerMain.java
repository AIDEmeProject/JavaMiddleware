package application;

import application.trace.TraceComputationServlet;
import application.trace.TraceInitializationServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;


import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;

import java.util.EnumSet;

public class ApplicationServerMain {


    public static void main(String[] args) throws Exception {


        Server server = new Server();

        ServerConnector http = new ServerConnector(server);

        http.setHost("0.0.0.0");
        http.setPort(7060);

        server.addConnector(http);
        ServletContextHandler handler = new ServletContextHandler(server, "/");

        handler.setSessionHandler(new SessionHandler());

        handler.addServlet(NewSessionServlet.class, "/new-session");
        handler.addServlet(ChooseSessionOptionServel.class, "/choose-options");

        handler.addServlet(GetSpecificDataToLabelServlet.class, "/get-specific-point-to-label");
        handler.addServlet(DataPointsWereLabeledServlet.class, "/data-point-were-labeled");
        handler.addServlet(TSMDataPointsWereLabeledServlet.class, "/tsm-data-point-were-labeled");
        handler.addServlet(GetLabeledDatasetServlet.class, "/get-labeled-dataset");
        handler.addServlet(ModelVisualizationServlet.class, "/get-visualization-data");
        handler.addServlet(FakePointInitialSampling.class, "/fake-point-initial-sampling");
        handler.addServlet(LabelPointForDecisionBoundaryServlet.class, "/get-decision-boundaries");
        handler.addServlet(getFakePointGridServlet.class, "/get-fake-point-grid");
        handler.addServlet(GetModelPredictionOverFakePointGridServlet.class, "/get-model-predictions-over-grid-point");
        handler.addServlet(getTSMPredictionOverFakePointGridServlet.class, "/get-tsm-predictions-over-grid-point");
        handler.addServlet(TraceInitializationServlet.class, "/start-trace");
        handler.addServlet(TraceComputationServlet.class, "/get-next-traces");


        handler.addServlet(GetPointToLabelFromFilteringServlet.class, "/get-points-by-filtering");
        //handler.addServlet(GetModelPredictionOverRealDatasetServlet.class, "/get-model-prediction-over-dataset");


        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("./static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, handler });
        server.setHandler(handlers);

        FilterHolder cors = handler.addFilter(CrossOriginFilter.class,"/*",EnumSet.allOf(DispatcherType.class));
        //FilterHolder cors = handler.addFilter(FilterHolder.class,"/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        server.start();
        server.join();

    }
}