package cs601.travelHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

@SuppressWarnings("serial")
public class SaveHotel extends BaseServlet {

    private DatabaseConnector db;


    private String INSERT_INTO_HOTEL =
            "insert into saveHotel (hotelId,hotelName,user) values(?,?,?);";

    public SaveHotel() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    /**
     * Method to save hotel into database
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            checkLoginState(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String hotelName = request.getParameter("hotelName");
        String hotelId = request.getParameter("hotelId");

        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("user");

        try {
            Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_INTO_HOTEL);
            statement.setString(1, hotelId);
            statement.setString(2, hotelName);
            statement.setString(3, user);

            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        response.sendRedirect("/HotelDetailServlet?hotelId=" + hotelId);
    }
}




