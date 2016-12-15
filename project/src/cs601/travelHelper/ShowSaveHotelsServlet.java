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
import java.util.HashSet;
import java.util.List;

/**
 * servlet to fetch saved hotels
 */

@SuppressWarnings("serial")
public class ShowSaveHotelsServlet extends BaseServlet {

    private DatabaseConnector db;


    private String FETCH_HOTELS_SQL =
            "select DISTINCT hotelId,hotelName from saveHotel where user= ?;";

    public ShowSaveHotelsServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    /**
     * handle request works with velocity to handle http request
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
        HttpSession session=request.getSession();
        String user= (String) session.getAttribute("user");
        prepareResponse("SavedHotel",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/saveHotel.vm");
        context.put("hotels", getHotelRows(user));
        //context.put("application", "Test Application");

        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }

    /**
     * fetching saved hotel data from database,
     * @param user
     * @return
     */
    private HashSet<String> getHotelRows(String user) {
        HashSet<String> hotelRows = new HashSet<>();
        try {

            Connection connection = db.getConnection();

            PreparedStatement statement = connection.prepareStatement(FETCH_HOTELS_SQL);
            statement.setString(1,user);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int hotelId = rs.getInt("hotelId");
                String HotelName = rs.getString("hotelName");

                hotelRows.add(toTableRow(hotelId, HotelName));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
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
    private String toTableRow(int hotelId, String hotelName) {
        String url = "HotelDetailServlet?hotelId="+hotelId+"&hotelName="+hotelName;
        return String.format("<tr>" +
                        "<td><a href=\"%s\">%s</a></td>" +
                        "</tr>",
                url, hotelName);

    }


}