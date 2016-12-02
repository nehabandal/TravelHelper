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
public class ReviewServlet extends BaseServlet {

    public static final String REVIEW_QUERY_SQL = "select reviewTitle,review,username,rating,date from reviewData where hotelId= ? order by date DESC ";

    private DatabaseConnector db;

    public ReviewServlet() {
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

        prepareResponse("review",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/hotel_detail.vm");
        PrintWriter out=null;
        try {
            out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.put("hotelId", hotelId);
        context.put("reviews", getReviewRows(hotelId));
       // context.put("attractions", getAttractionRows(hotelId));

        finishResponse(response);
        return template;
    }

    private List<String> getReviewRows(int hotelId) {
        List<String> reviewRows = new ArrayList<>();
        try {
            Connection connection = db.getConnection();

            PreparedStatement stmt = connection.prepareStatement(REVIEW_QUERY_SQL);
            stmt.setString(1, String.valueOf(hotelId));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String reviewTitle = rs.getString("reviewTitle");
                String review = rs.getString("review");
                String username = rs.getString("username");
                double rating = rs.getDouble("rating");
                String date = rs.getString("date");

                reviewRows.add("<tr><td>" + reviewTitle + "</td><td>" + review + "</td><td>" + username + "</td><td>" + rating + "</td><td>" + date + "</td></tr>");

            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviewRows;
    }

}
