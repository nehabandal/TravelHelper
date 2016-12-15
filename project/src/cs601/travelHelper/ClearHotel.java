package cs601.travelHelper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Servlet to delete saved hotel data
 */

@SuppressWarnings("serial")
public class ClearHotel extends BaseServlet {
    private DatabaseConnector db;
    private String CLEAR_HOTEL_LIST = "delete from saveHotel where user=?;";

    public ClearHotel() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            db = null;
        }

    }

    /**
     * Get method to delete saved hotel data
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
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("user");
        try {
            Connection connection = db.getConnection();

            PreparedStatement statement = connection.prepareStatement(CLEAR_HOTEL_LIST);
            statement.setString(1, user);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("/ShowSaveHotelsServlet");
    }
}




