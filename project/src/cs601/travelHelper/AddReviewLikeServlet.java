package cs601.travelHelper;

import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by npbandal on 12/13/16.
 */
public class AddReviewLikeServlet extends RestApiServlet {
    public static final String INSERT_REVIEW_LIKE_SQL = "insert into reviewLikes (reviewId, user) values (?, ?);";

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");
        String reviewId = request.getParameter("reviewId");
        addReviewLike(reviewId, username);
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("Review: " + reviewId + " liked by user: " + username);
    }

    /**
     * Servlet to add review likes into database
     * @param reviewId
     * @param username
     * @throws SQLException
     */
    private void addReviewLike(String reviewId, String username) throws SQLException {
        Connection connection = db.getConnection();

        PreparedStatement stmt = connection.prepareStatement(INSERT_REVIEW_LIKE_SQL);
        stmt.setString(1, String.valueOf(reviewId));
        stmt.setString(2, String.valueOf(username));

        int result = stmt.executeUpdate();

        System.out.println("ExecuteUpdate return value: " + result);
        connection.close();
    }

}
