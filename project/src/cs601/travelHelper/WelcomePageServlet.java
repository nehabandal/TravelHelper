package cs601.travelHelper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

public class WelcomePageServlet extends BaseServlet {
    private String FETCH_HOTELS_SQL =
            "select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, hotelData.state, hotelData.country, hotelData.latitude, hotelData.longitude, avg(rating) as avgRating " +
                    "from hotelData " +
                    "LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId " +
                    "group by(hotelData.hotelId)";

    private DatabaseConnector db;
    // private String Login_tracking =""
    private static final String Insert_SQL_LoginTrack = "insert into login_tracking(user,ts) values(?,now());";
    private static final String Select_SQL_LoginTrack = "select ts from login_tracking where user=? ORDER by ts desc;";


    public WelcomePageServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    /**
     * Method for welcome page servlet
     * And here we are keeping track of last login through login_tracking table in database
     * @param request
     * @param response
     * @param context
     * @return
     */
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
        String timestamp = null;
        prepareResponse("Hotel", response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/welcome_page.vm");
        try {
            Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(Select_SQL_LoginTrack);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                timestamp = rs.getString("ts");
            }

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(Insert_SQL_LoginTrack);
            statement.setString(1, username);

            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        context.put("username", username);
        context.put("timestamp", timestamp);
        finishResponse(response);
        context.put("hotelData", fetchHotels());
        return template;
    }

    /**
     *
     * @return
     */
    private List<Map<String, String>> fetchHotels() {
        List<Map<String, String>> result = new ArrayList<>();
        Connection connection = null;
        try {
            connection = db.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(FETCH_HOTELS_SQL);

            while (rs.next()) {
                Map<String, String> hotel = new HashMap<>();
                hotel.put("hotelId", rs.getString("hotelId"));
                hotel.put("hotelName", rs.getString("hotelName"));
                hotel.put("address", rs.getString("address"));
                hotel.put("city", rs.getString("city"));
                hotel.put("state", rs.getString("state"));
                hotel.put("country", rs.getString("country"));
                hotel.put("avgRating", rs.getString("avgRating"));
                hotel.put("latitude", rs.getString("latitude"));
                hotel.put("longitude", rs.getString("longitude"));

                result.add(hotel);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}