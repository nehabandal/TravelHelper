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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

@SuppressWarnings("serial")
public class ClearHotel extends BaseServlet {

    private DatabaseConnector db;

    private String CLEAR_HOTEL_LIST =
            "delete from saveHotel where user=?;";

    public ClearHotel() {
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
        String user = (String) session.getAttribute("user");

        prepareResponse("ClearHotel", response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();


        Template template = ve.getTemplate("web/templates/welcome_page.vm");


        try {
            Connection connection = db.getConnection();

            PreparedStatement statement = connection.prepareStatement(CLEAR_HOTEL_LIST);
            statement.setString(1, user);


            statement.executeUpdate();

connection.close();
        }

        catch (Exception e) {
            System.out.println(e);
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        }
        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }
}




