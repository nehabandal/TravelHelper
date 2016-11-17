package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by npbandal on 11/15/16.
 */
public class ReviewsServlet extends BaseServlet {
    public static final String REVIEW_QUERY_SQL = "select reviewTitle,review,username,rating from reviewData where hotelId= ?";
    private DatabaseConnector db;

    public ReviewsServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLoginState(request, response);
        int id = Integer.parseInt(request.getParameter("hotelId"));
        PrintWriter out = response.getWriter();
        out.println(id);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        out.println("<html><body>");
        try {
            Connection connection = db.getConnection();

            PreparedStatement stmt = connection.prepareStatement(REVIEW_QUERY_SQL);
            stmt.setString(1, String.valueOf(id));

            ResultSet rs = stmt.executeQuery();
            out.println("<table border=1 width=50% height=50%>");
            out.println("<tr><th>ReviewTitle</th><th>Review</th><th>Username</th><th>Rating</th><tr>");
            while (rs.next()) {
                String reviewTitle = rs.getString("reviewTitle");
                String review = rs.getString("review");
                String username = rs.getString("username");
                double rating = rs.getDouble("rating");

                out.println("<tr><td>" + reviewTitle + "</td><td>" + review + "</td><td>" + username + "</td><td>" + rating + "</td></tr>");
                //  +"</td><td>"  + "</td><td>" + "</td></tr>");
            }
            // response.sendRedirect();
            out.println("<a href=\"logout.html\">Logout</a>");
            out.println("</table>");
            out.println("</html></body>");
            connection.close();
        } catch (Exception e) {
            out.println("error");
        }


    }
}
