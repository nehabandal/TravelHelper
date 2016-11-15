package cs601.travelHelper;


import cs601.hotelapp.HotelDataBuilder;
import cs601.hotelapp.ThreadSafeHotelData;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Demonstrates how to use Jetty, servlets and JDBC for user registration. This is a
 * simplified example, and **NOT** secure. 
 * Modified from the example by Prof. Engle.
 */
public class TravelHelperServer {
    private static int PORT = 8080;

    public static void main(String[] args) {
        TravelHelperServer server = new TravelHelperServer();
        ThreadSafeHotelData tdsafe = new ThreadSafeHotelData();
        HotelDataBuilder data = new HotelDataBuilder(tdsafe);
       //data.loadHotelInfo("input/hotels200.json");
       // data.loadReviews(Paths.get("input/reviews"));

        server.start();

    }

    public void start() {
        Server server = new Server(PORT);

        ResourceHandler resourceHandler = getResourceHandler();
        HandlerList handlers = new HandlerList();
        server.setHandler(handlers);
        handlers.setHandlers(new Handler[] {
                resourceHandler,
                getServletContextHandler(),

                new DefaultHandler()
        });

        try {
            server.start();
            server.join();

        } catch (Exception ex) {
            System.out.println("An exception occurred while running the server. ");
            System.exit(-1);
        }
    }

    private ServletContextHandler getServletContextHandler() {
        ServletContextHandler servletContexthandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContexthandler.addServlet(LoginServlet.class, "/LoginServlet");
        servletContexthandler.addServlet(LogoutServlet.class, "/LogoutServlet");
        servletContexthandler.addServlet(HotelsServlet.class, "/session");
        servletContexthandler.addServlet(RegisterServlet.class, "/RegisterServlet");


        return servletContexthandler;
    }

    private ResourceHandler getResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "login.html" });

        resourceHandler.setResourceBase("web");
        return resourceHandler;
    }

}