package cs601.travelHelper;


import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Demonstrates how to use Jetty, servlets and JDBC for user registration. This is a
 * simplified example, and **NOT** secure. 
 * Modified from the example by Prof. Engle.
 */
public class TravelHelperServer {
    private static int PORT = 8080;

    public static void main(String[] args) {
        TravelHelperServer server = new TravelHelperServer();
        server.start();
    }

    public void start() {
        Server server = new Server(PORT);

        ResourceHandler resourceHandler = getResourceHandler();
        ServletHandler servletHandler = getServletHandler();

        HandlerList handlers = new HandlerList();
        server.setHandler(handlers);
        handlers.setHandlers(new Handler[] { resourceHandler, servletHandler, new DefaultHandler() });

        try {
            server.start();
            server.join();

        } catch (Exception ex) {
            System.out.println("An exception occurred while running the server. ");
            System.exit(-1);
        }
    }

    private ServletHandler getServletHandler() {
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(RegisterServlet.class, "/RegisterServlet");
        // handler.addServletWithMapping(LoginServlet.class, "/login");
        // other servlets can be added as needed such as LoginServlet etc.
        return servletHandler;
    }

    private ResourceHandler getResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "login.html" });

        resourceHandler.setResourceBase("web");
        return resourceHandler;
    }
}