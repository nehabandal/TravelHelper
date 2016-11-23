package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by npbandal on 11/15/16.
 */
public class ReviewsServlet extends BaseServlet {
    public static final String REVIEW_QUERY_SQL = "select reviewTitle,review,username,rating,date from reviewData where hotelId= ? order by date";
    private DatabaseConnector db;

    public ReviewsServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET method for fetching all reviews for selected hotel, select resultset from database and display reviews for hotel
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLoginState(request, response);
        HttpSession session = request.getSession();
        int hotelId = Integer.parseInt(request.getParameter("hotelId"));
        session.setAttribute("hotelId", hotelId);

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        out.println("<html><body>");
        try {
            Connection connection = db.getConnection();

            PreparedStatement stmt = connection.prepareStatement(REVIEW_QUERY_SQL);
            stmt.setString(1, String.valueOf(hotelId));

            ResultSet rs = stmt.executeQuery();
            out.println("<table border=1 width=60% height=50%>");
            out.println("<tr><th>ReviewTitle</th><th>Review</th><th>Username</th><th>Rating</th><th>Date</th><tr>");
            while (rs.next()) {
                String reviewTitle = rs.getString("reviewTitle");
                String review = rs.getString("review");
                String username = rs.getString("username");
                double rating = rs.getDouble("rating");
                String date = rs.getString("date");

                out.println("<tr><td>" + reviewTitle + "</td><td>" + review + "</td><td>" + username + "</td><td>" + rating + "</td><td>" + date + "</td></tr>");

            }

            out.println("<a href=\"/LogoutServlet\">Logout</a> &nbsp");
            out.println("<a href=\"AddReview?hotelId=" + hotelId + "\">Add Review</a>");
            out.println("</table>");
            out.println("</html></body>");
            connection.close();
        } catch (Exception e) {
            out.println("error");
        }


    }
}
