package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by npbandal on 12/13/16.
 */
public class GetReviewLikesServlet extends RestApiServlet {
    static final String GET_REVIEW_LIKES_SQL = "SELECT count(*) from reviewLikes WHERE reviewId = ?;";

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String reviewId = Objects.requireNonNull(request.getParameter("reviewId"));

        int reviewCount = getReviewCount(reviewId);
        Map<String, String> results = new HashMap<>();
        results.put("result", String.valueOf(reviewCount));
        resultJson(response, results);

        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("# Likes for review: " + reviewCount);
    }

    private int getReviewCount(String reviewId) throws SQLException {
        Connection connection = db.getConnection();

        PreparedStatement stmt = connection.prepareStatement(GET_REVIEW_LIKES_SQL);
        stmt.setString(1, String.valueOf(reviewId));

        ResultSet rs = stmt.executeQuery();
        int count = 0;
        while (rs.next()) {
             count = rs.getInt(1);
        }
        connection.close();
        return count;
    }

}
