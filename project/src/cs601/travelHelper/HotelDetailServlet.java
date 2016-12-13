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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 11/15/16.
 */
public class HotelDetailServlet extends BaseServlet {
    private String FETCH_HOTEL_SQL =
            "select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, hotelData.state, hotelData.country, avg(rating) as avgRating " +
                    "from hotelData " +
                    "LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId " +
                    "where hotelData.hotelId = ?";

    private DatabaseConnector db;

    public HotelDetailServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
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
        int hotelId = Integer.parseInt(request.getParameter("hotelId"));
        session.setAttribute("hotelId", hotelId);

        prepareResponse("review",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/hotel_detail.vm");
        getHotelInfo(hotelId, context);

        context.put("expediaUrl", "https://www.expedia.com/h" + hotelId + ".hotel-information");
        finishResponse(response);
        return template;
    }

    private void getHotelInfo(int hotelId, Context context) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(FETCH_HOTEL_SQL);
            statement.setString(1, String.valueOf(hotelId));
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                context.put("hotelName", rs.getString("hotelName"));
                context.put("address", rs.getString("address"));
                context.put("city", rs.getString("city"));
                context.put("state", rs.getString("state"));
                context.put("country", rs.getString("country"));
                context.put("avgRating", rs.getDouble("avgRating"));
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
    }
}
