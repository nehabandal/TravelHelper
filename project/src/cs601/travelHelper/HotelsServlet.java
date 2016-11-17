package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

@SuppressWarnings("serial")
public class HotelsServlet extends BaseServlet {

    private DatabaseConnector db;
    private String FETCH_HOTELS_SQL =
            "select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, avg(rating) as avgRating " +
                    "from hotelData " +
                    "LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId " +
                    "group by(hotelData.hotelId)";

    public HotelsServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLoginState(request, response);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        Connection connection = null;
        try {
            connection = db.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(FETCH_HOTELS_SQL);

            out.println("<table border=1 width=50% height=50%>");
            out.println("<tr><th>HotelName</th><th>Address</th><th>City</th><th>Rating</th><tr>");

            while (rs.next()) {
                int hotelId = rs.getInt("hotelId");
                String hotelName = rs.getString("hotelName");
                String address = rs.getString("address");
                String city = rs.getString("city");
                double avgRating = rs.getDouble("avgRating");

                out.println(toTableRow(hotelId, hotelName, address, city, avgRating));
            }

            out.println("<a href=\"logout.html\">Logout</a>");
            out.println("</table>");
            out.println("</html></body>");

        } catch (Exception e) {
            out.println("error");
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

    private String toTableRow(int hotelId, String hotelName, String address, String city, double avgRating) {
        String url = "ReviewsServlet?hotelId=" + hotelId;
        return String.format("<tr>" +
                        "<td><a href=\"%s\">%s</a></td>" +
                        "<td>%s</td>" +
                        "<td>%s</td>" +
                        "<td>%1.1f</td>" +
                        "</tr>",
                url, hotelName, address, city, avgRating);
    }



}