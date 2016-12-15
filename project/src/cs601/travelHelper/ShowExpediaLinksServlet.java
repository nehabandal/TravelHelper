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
import java.util.HashSet;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

public class ShowExpediaLinksServlet extends BaseServlet {

    private DatabaseConnector db;

    private String FETCH_LINKS_SQL =
            "select hotelData.hotelName, expediaLinks.url " +
                    "from hotelData INNER JOIN expediaLinks " +
                    "ON hotelData.hotelID = expediaLinks.hotelId " +
                    "where expediaLinks.user= ?;";

    public ShowExpediaLinksServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    /**
     * Method to handle http request from velocity
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
        Template template = ve.getTemplate("web/templates/expedia-links.vm");
        context.put("data", getRows(user));
        finishResponse(response);
        return template;
    }

    /**
     * Method to fetch expedia link data
     * @param user
     * @return
     */
    private HashSet<String> getRows(String user) {
        HashSet<String> hotelRows = new HashSet<>();
        try {
            Connection connection = db.getConnection();

            PreparedStatement statement = connection.prepareStatement(FETCH_LINKS_SQL);
            statement.setString(1, user);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String hotelName = rs.getString(1);
                String url = rs.getString(2);

                hotelRows.add(toTableRow(hotelName, url));
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
    private String toTableRow(String hotelName, String url) {
        return String.format("<td>%s</td><td><a href=\"%s\">%s</a></td>", hotelName, url, url);

    }


}