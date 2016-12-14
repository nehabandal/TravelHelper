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
public class ShowHotelsServlet extends BaseServlet {

    private DatabaseConnector db;
    private String FETCH_HOTELS_SQL =
            "select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, hotelData.state, hotelData.country, avg(rating) as avgRating " +
                    "from hotelData " +
                    "LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId " +
                    "group by(hotelData.hotelId)";

    public ShowHotelsServlet() {
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
        prepareResponse("Hotel",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/hotels.vm");
        PrintWriter out=null;
        try {
            out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.put("hotels", getHotelRows());
        //context.put("application", "Test Application");

        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }

    private List<String> getHotelRows() {
        List<String> hotelRows = new ArrayList<>();
        Connection connection = null;
        try {
            connection = db.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(FETCH_HOTELS_SQL);

            while (rs.next()) {
                int hotelId = rs.getInt("hotelId");
                String hotelName = rs.getString("hotelName");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String country = rs.getString("country");
                double avgRating = rs.getDouble("avgRating");


                hotelRows.add(toTableRow(hotelId, hotelName, address, city, state, country, avgRating));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hotelRows;
    }

    /**
     * Passing values in URL for displaying reviews for hotel, and displaying list of hotels
     *
     * @param hotelId
     * @param hotelName
     * @param address
     * @param city
     * @param state
     * @param country
     * @param avgRating
     * @return
     */
    private String toTableRow(int hotelId, String hotelName, String address, String city, String state, String country, double avgRating) {
        String url = "HotelDetailServlet?hotelId="+hotelId;
      //  String url = "ReviewServlet?hotelId=" + hotelId;
        return String.format(
                        "<td><a href=\"%s\">%s</a></td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%1.1f</td>",
                url, hotelName, address, city, state, country, avgRating);

    }


}