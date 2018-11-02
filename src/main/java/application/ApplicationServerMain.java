package application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;


import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;

import java.util.EnumSet;

public class ApplicationServerMain {


    public static void main(String[] args) throws Exception {


        Server server = new Server(7060);

        ServletContextHandler handler = new ServletContextHandler(server, "/");

        handler.setSessionHandler(new SessionHandler());


        handler.addServlet(NewSessionServlet.class, "/new-session");
        handler.addServlet(ChooseSessionOptionServel.class, "/choose-options");
        handler.addServlet(DataPointsWereLabeledServlet.class, "/data-point-were-labeled");

        FilterHolder cors = handler.addFilter(CrossOriginFilter.class,"/*",EnumSet.allOf(DispatcherType.class));
        //FilterHolder cors = handler.addFilter(FilterHolder.class,"/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        server.start();

    }
}