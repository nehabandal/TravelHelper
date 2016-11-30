package cs601.travelHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static final String REVIEW_UPDATE_QUERY_SQL = "update reviewData set reviewTitle = ?, review = ?, rating = ?";

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
            stmt.setString(1, String.valueOf(session.getAttribute("user")));
            stmt.setString(2, String.valueOf(session.getAttribute("hotelId")));
            ResultSet rs = stmt.executeQuery();
            out.println("<a href=\"/LogoutServlet\">Logout</a> &nbsp");
            out.println("<h1> MODIFY YOUR REVIEW HERE </h1>");
            while (rs.next()) {
                String reviewTitle = rs.getString("reviewTitle");
                String review = rs.getString("review");
                double rating = rs.getDouble("rating");
                String reviewId = rs.getString("reviewId");
                out.println("<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +

                        "\n" +
                        "<form method=\"post\" id=\"modifyReview\"action=\"ModifyReview\">\n" +
                        " Title:<br>\n" +
                        "  <input type=\"text\" name=\"title\" value=\"" + reviewTitle + "\">" + "\n" +
                        "  <br>Comments:<br>\n" +
                        "  <textarea name=\"comments\" rows=\"4\" cols=\"50\" >" + review +
                        "</textarea>" +
                        "  <br>Rating:<br>\n" +
                        "  <input type=\"text\" name=\"rating\" value=" + rating + ">\n" +
                        "  <input type=\"hidden\" name=\"reviewId\" value=" + reviewId + ">\n" +
                        "  <br><br>\n" +
                        "  <input type=\"submit\" value=\"Modify Review\">\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>\n" +
                        "\n");

            }
            out.println("</body></html>");
            connection.close();
        } catch (Exception e) {
            out.println("error");
        }


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
            response.sendRedirect("/HotelDetailServlet?hotelId=" + hotelId);
        }
    }


}


