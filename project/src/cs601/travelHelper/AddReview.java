package cs601.travelHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by npbandal on 11/16/16.
 */
public class AddReview extends BaseServlet {
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    private final String innerHtml =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<body>\n" +
                    "\n" +
                    "<form method=\"post\" id=\"addReview\"action=\"AddReview\">\n" +
                    "  Title:<br>\n" +
                    "  <input type=\"text\" name=\"title\">\n" +
                    "  <br>Comments:<br>\n" +
                    "  <textarea name=\"comments\" rows=\"4\" cols=\"50\">" +
                    "</textarea>" +
                    // "  <input type=\"text\" name=\"comments\" size=\"200\">\n" +
                    "  <br>Rating:<br>\n" +
                    "  <input type=\"text\" name=\"rating\">\n" +
                    "  <br><br>\n" +
                    "  <input type=\"submit\" value=\"Add Review\">\n" +
                    "</form>\n" +

                    "</body>\n" +
                    "</html>\n" +
                    "\n";

    /**
     * Get method for adding new review it will display form for review field
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLoginState(request, response);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        out.println(innerHtml);
    }

    /**
     * Post method for adding new review to reviewData table
     * Fill fields from Get method and submit request with data and add review to database
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        checkLoginState(request, response);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

        HttpSession session = request.getSession();

        int hotelId = (int) session.getAttribute("hotelId");
        String username = (String) session.getAttribute("user");

        String title = request.getParameter("title");
        String comments = request.getParameter("comments");
        String rating = request.getParameter("rating");
        Date date = new Date();
        String datetest = format.format(date);
        SecureRandom random = new SecureRandom();
        String reviewId = new BigInteger(130, random).toString(32);
        Status status = dbhandler.addReviewDB(
                reviewId,
                String.valueOf(hotelId),
                title,
                comments,
                username,
                datetest,
                Double.parseDouble(rating));

        if (status == Status.ERROR) {
            response.getWriter().println("Please enter rating between 1 to 5");
        }
        if (status == Status.OK) { // registration was successful
            response.getWriter().println("Review added successfully!");
            response.sendRedirect("/ReviewServlet?hotelId=" + hotelId);
        }
    }
}
