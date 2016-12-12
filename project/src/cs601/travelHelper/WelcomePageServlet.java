package cs601.travelHelper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

@SuppressWarnings("serial")
public class WelcomePageServlet extends BaseServlet {

    private DatabaseConnector db;
    private String FETCH_HOTELS_SQL =
            "select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, hotelData.state, hotelData.country, avg(rating) as avgRating " +
                    "from hotelData " +
                    "LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId " +
                    "group by(hotelData.hotelId)";

    public WelcomePageServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    public Template handleRequest(HttpServletRequest request,
                                  HttpServletResponse response, Context context) {
        try {
            checkLoginState(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpSession session = request.getSession();
//       String username=request.getParameter("user");
//        session.setAttribute("username",username);
        String username = (String) session.getAttribute("user");

        prepareResponse("Hotel", response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/welcome_page.vm");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // context.put("hotels", getHotelRows());
        //context.put("application", "Test Application");
        context.put("username", username);
        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }
}