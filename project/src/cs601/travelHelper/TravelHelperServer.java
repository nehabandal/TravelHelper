package cs601.travelHelper;


import cs601.hotelapp.HotelDataBuilder;
import cs601.hotelapp.ThreadSafeHotelData;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Demonstrates how to use Jetty, servlets and JDBC for user registration. This is a
 * simplified example, and **NOT** secure.
 * Modified from the example by Prof. Engle.
 */
public class TravelHelperServer {
    private static int PORT = 2000;

    public static void main(String[] args) {
//        loadData();
        TravelHelperServer server = new TravelHelperServer();
        server.start();
    }

    private static void loadData() {
        ThreadSafeHotelData tdsafe = new ThreadSafeHotelData();
        HotelDataBuilder data = new HotelDataBuilder(tdsafe);
//        InsertExisting newinsert = new InsertExisting();

        data.loadHotelInfo("input/hotels200.json");
//        data.loadReviews(Paths.get("input/reviews"));
//        data.loadHotelInfo("input/hotels2001st.json");
//        data.fetchAttractions(2);
    }

    public void start() {
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        Server server = new Server(PORT);

        ServletContextHandler staticContext = new ServletContextHandler(ServletContextHandler.SESSIONS);

        staticContext.setContextPath("/static");
        ResourceHandler resourceHandler = new ResourceHandler();

        resourceHandler.setResourceBase("./web/public");
        resourceHandler.setDirectoriesListed(true);
        staticContext.setHandler(resourceHandler);

        ServletContextHandler context = getServletContextHandler();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{staticContext, context});
        server.setHandler(contexts);

        context.setAttribute("templateEngine", ve);
        try {
            server.start();
            server.join();

        } catch (Exception ex) {
            System.out.println("An exception occurred while running the server. ");
            System.exit(-1);
        }
    }

    /**
     * Method which adds all servlet
     *
     * @return
     */
    private ServletContextHandler getServletContextHandler() {
        ServletContextHandler servletContexthandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContexthandler.addServlet(LoginServlet.class, "/");
        servletContexthandler.addServlet(LoginServlet.class, "/LoginServlet");
        servletContexthandler.addServlet(LogoutServlet.class, "/LogoutServlet");
        servletContexthandler.addServlet(RegisterServlet.class, "/RegisterServlet");
        servletContexthandler.addServlet(HotelDetailServlet.class, "/HotelDetailServlet");
        servletContexthandler.addServlet(WelcomePageServlet.class, "/WelcomePageServlet");
        servletContexthandler.addServlet(AddReview.class, "/AddReview");
        servletContexthandler.addServlet(ModifyReview.class, "/ModifyReview");
        servletContexthandler.addServlet(ShowHotelsServlet.class, "/ShowHotelsServlet");
        servletContexthandler.addServlet(ShowRecomendedHotelsServlet.class, "/ShowRecomendedHotelsServlet");
        servletContexthandler.addServlet(ReviewServlet.class, "/ReviewServlet");
        servletContexthandler.addServlet(AttractionServlet.class, "/AttractionServlet");
        servletContexthandler.addServlet(HomePage.class, "/HomePage");
        servletContexthandler.addServlet(NameSearch.class, "/NameSearch");
        servletContexthandler.addServlet(SaveHotel.class, "/SaveHotel");
        servletContexthandler.addServlet(ShowSaveHotelsServlet.class, "/ShowSaveHotelsServlet");
        servletContexthandler.addServlet(ShowExpediaLinksServlet.class, "/ShowExpediaLinksServlet");
        servletContexthandler.addServlet(DeleteExpediaLinksServlet.class, "/DeleteExpediaLinksServlet");
        servletContexthandler.addServlet(ClearHotel.class, "/ClearHotel");
        servletContexthandler.addServlet(AddReviewLikeServlet.class, "/api/addLike");
        servletContexthandler.addServlet(GetReviewLikesServlet.class, "/api/getLikeCount");
        servletContexthandler.addServlet(AddExpediaLinksServlet.class, "/api/expedia");

        return servletContexthandler;
    }
}