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
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 */

@SuppressWarnings("serial")
public class WelcomePageServlet extends BaseServlet {

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
        String timestamp=null;
        prepareResponse("Hotel", response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/welcome_page.vm");
        PrintWriter out = null;
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

        // context.put("hotels", getHotelRows());
        //context.put("application", "Test Application");
        context.put("username", username);
        context.put("timestamp",timestamp);
        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }
}