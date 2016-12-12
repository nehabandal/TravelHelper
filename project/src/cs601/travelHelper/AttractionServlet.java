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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 11/15/16.
 */
public class AttractionServlet extends BaseServlet {
    public static final String ATTRACTION_QUERY_SQL = "select attractionName,rating,address from attractionData where hotelId= ? order by rating";
    private DatabaseConnector db;

    public AttractionServlet() {
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
        int hotelId = (int) session.getAttribute("hotelId");
        session.setAttribute("hotelId", hotelId);

        prepareResponse("attraction",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/attractions.vm");
        PrintWriter out=null;
        try {
            out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.put("hotelId", hotelId);
        //context.put("reviews", getReviewRows(hotelId));
        context.put("attractions", getAttractionRows(hotelId));

        finishResponse(response);
        return template;
    }

    private List<String> getAttractionRows(int hotelId) {
        List<String> attractionRows = new ArrayList<>();
        try {
            Connection connection = db.getConnection();

            PreparedStatement stmt = connection.prepareStatement(ATTRACTION_QUERY_SQL);
            stmt.setString(1, String.valueOf(hotelId));

            ResultSet rs = stmt.executeQuery();
            // out.println("<table border=1 width=60% height=50%>");
            while (rs.next()) {
                String attractionName = rs.getString("attractionName");
                String address = rs.getString("address");
                double rating = rs.getDouble("rating");
                //String date = rsat.getString("date");

                attractionRows.add("<tr><td>" + attractionName + "</td><td>&nbsp" + address + "</td><td>&nbsp" + rating + "</td></tr><br>");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attractionRows;
    }
}
