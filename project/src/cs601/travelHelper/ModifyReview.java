package cs601.travelHelper;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by npbandal on 11/23/16.
 */
public class ModifyReview extends BaseServlet {

    private DatabaseConnector db;
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

    public ModifyReview() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String REVIEW_QUERY_SQL = "select reviewTitle,review,username,reviewId,rating,date from reviewData where username= ? and hotelId= ? order by date";

    public Template handleRequest(HttpServletRequest request,
                                  HttpServletResponse response, Context context) {
        try {
            checkLoginState(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        Template template = ve.getTemplate("web/templates/modify-review.vm");
        HttpSession session = request.getSession();

        try {
            context.put("reviews", getExistingReviews(
                    String.valueOf(session.getAttribute("user")),
                    String.valueOf(session.getAttribute("hotelId"))
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return template;
    }

    public List<Map<String, String>> getExistingReviews(String user, String hotelId) throws SQLException {
        Connection connection = db.getConnection();

        PreparedStatement stmt = connection.prepareStatement(REVIEW_QUERY_SQL);
        stmt.setString(1, user);
        stmt.setString(2, hotelId);

        ResultSet rs = stmt.executeQuery();
        List<Map<String, String>> results = new ArrayList<>();
        while (rs.next()) {
            Map<String, String> reviewMap = new HashMap<>();
            reviewMap.put("reviewTitle", rs.getString("reviewTitle"));
            reviewMap.put("review", rs.getString("review"));
            reviewMap.put("rating", rs.getString("rating"));
            reviewMap.put("reviewId", rs.getString("reviewId"));

            results.add(reviewMap);
        }
        connection.close();
        return results;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        checkLoginState(request, response);


        HttpSession session = request.getSession();

        int hotelId = (int) session.getAttribute("hotelId");
        String username = (String) session.getAttribute("user");

        String title = request.getParameter("title");
        String comments = request.getParameter("comments");
        String rating = request.getParameter("rating");
        String reviewId = request.getParameter("reviewId");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        Date date = new Date();
        String datetest = format.format(date);

        Status status = dbhandler.UpdateReviewDB(
                title, comments, Double.parseDouble(rating), username, reviewId, datetest);
        if (status == Status.ERROR) {
            response.getWriter().println("Please enter rating between 1 to 5");
        }
        if (status == Status.OK) { // registration was successful
            response.getWriter().println("Review updated successfully!");
            response.sendRedirect("/ReviewServlet?hotelId=" + hotelId);
        }
    }


}


