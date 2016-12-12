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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public Template handleRequest(HttpServletRequest request,
                                  HttpServletResponse response, Context context) {
        try {
            checkLoginState(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String hotelName = request.getParameter("hotelName");
        String hotelId = request.getParameter("hotelId");
        PrintWriter out= null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print(hotelName);
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("user");

        prepareResponse("SaveHotel", response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();


        Template template = ve.getTemplate("src/cs601/travelHelper/HotelDetailServlet.java");


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

        try {
            response.sendRedirect("/HotelDetailServlet?hotelId="+hotelId+"&hotelName="+hotelName);
        } catch (IOException e) {
            e.printStackTrace();
        }



        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
    }
}




