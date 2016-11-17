package cs601.travelHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
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
                    "  <input type=\"text\" name=\"comments\">\n" +
                    "  <br>Rating:<br>\n" +
                    "  <input type=\"text\" name=\"rating\">\n" +
                    "  <br>ID:<br>\n" +
                    "  <input type=\"text\" name=\"id\">\n" +
                    "  <br><br>\n" +
                    "  <input type=\"submit\" value=\"Add Review\">\n" +
                    "</form>\n" +
                    "</body>\n" +
                    "</html>\n" +
                    "\n";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLoginState(request, response);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        out.println(innerHtml);
    }

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
        String reviewId = request.getParameter("id");
        Date date= new Date();
        String datetest = format.format(date);


        Status status = dbhandler.addReviewDB(
                reviewId,
                String.valueOf(hotelId),
                title,
                comments,
                username,
                datetest,
                Double.parseDouble(rating));

        if (status == Status.OK) { // registration was successful
            response.getWriter().println("Review added successfully!");
            response.sendRedirect("/ReviewsServlet?hotelId=" + hotelId);
        } else {
            // if something went wrong
            // send a get request  (redirect to the same path)
            response.getWriter().println("Invalid Username or Password");
        }
    }
}
