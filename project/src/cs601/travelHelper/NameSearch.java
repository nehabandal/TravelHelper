package cs601.travelHelper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

@SuppressWarnings("serial")
public class NameSearch extends BaseServlet {

    private DatabaseConnector db;
    Connection connection = null;
    List<String> hotelRows = new ArrayList<>();
    private String FETCH_HOTELS_SQL =
            "select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, hotelData.state, hotelData.country, avg(rating) as avgRating " +
                    "from hotelData " +
                    "LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId " +
                    "where hotelData.hotelName LIKE ?";

    public NameSearch() {
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
        String hotelName=request.getParameter("hotelname");


        prepareResponse("Hotel",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/hotels.vm");


        try {
            connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(FETCH_HOTELS_SQL);
            statement.setString(1,"%" + hotelName + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int hotelId = rs.getInt("hotelId");
                String HotelName = rs.getString("hotelName");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String country = rs.getString("country");
                double avgRating = rs.getDouble("avgRating");


                hotelRows.add(toTableRow(hotelId, HotelName, address, city, state, country, avgRating));
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

        context.put("hotels", hotelRows);
        //context.put("application", "Test Application");

        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }

    private List<String> getHotelRows(String hotelname) {
        List<String> hotelRows = new ArrayList<>();
        Connection connection = null;
        try {
            connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(FETCH_HOTELS_SQL);
            statement.setString(1,hotelname);

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
        String url = "HotelDetailServlet?hotelId="+hotelId+"&hotelName="+hotelName;
      //  String url = "ReviewServletDate?hotelId=" + hotelId;
        return String.format("<tr>" +
                        "<td><a href=\"%s\">%s</a></td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%1.1f</td>" +
                        "</tr>",
                url, hotelName, address, city, state, country, avgRating);

    }


}