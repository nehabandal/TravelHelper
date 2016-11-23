package cs601.travelHelper;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A servlet that handles user registration. doGet() method displays an HTML form with a button and
 * two textfields: one for the username, one for the password.
 * doPost() processes the form: if the username is not taken, it adds user info to the database.
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {

    // DatabaseHandler interacts with the MySQL database
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    private String innerHtml =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"US-ASCII\">\n" +
                    "    <title>Login Page</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<form action=\"/LoginServlet\" method=\"post\">\n" +
                    "\n" +
                    "\n" +
                    "    <center>\n" +
                    "        <h1> Welcome to TravelHelper - A Hotel Review Website </h1><br>\n" +
                    "    <table border=1>\n" +
                    "        <tr><td>Enter Your Name :</td>\n" +
                    "            <td><input type=\"text\" name=\"user\" value=\"\"></td>\n" +
                    "        </tr>\n" +
                    "        <tr><td>Enter Your PassWord :</td>\n" +
                    "            <td><input type=\"password\" name=\"password\" value=\"\"></td>\n" +
                    "        </tr>\n" +
                    "        <tr><td align=center><input type=\"reset\" name=\"reset\" value=\"Refresh\"></td>\n" +
                    "            <td align=center><input type=\"submit\" name=\"Submit\" value=\"Login\" ></td>\n" +
                    "\n" +
                    "            </table>\n" +
                    "\n" +
                    "    </center>\n" +
                    "\n" +
                    "</form>\n" +
                    "\n" +
                    "<a href=\"/RegisterServlet\">Register</a>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n";

    /**
     * GET method which will check user is still login if yes then at any indirect URL take back to hotel list page
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            response.sendRedirect("/HotelsServlet");
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        out.println(innerHtml);
    }

    /**
     * POST method to submit login form,check user is authenticated and then allow login
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        prepareResponse("Login to Existing user", response);

        // Get data from the textfields of the html form
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        // sanitize user input to avoid XSS attacks:
        user = StringEscapeUtils.escapeHtml4(user);
        password = StringEscapeUtils.escapeHtml4(password);

        // add user's info to the database
        Status status = dbhandler.authenticateUser(user, password);

        if (status == Status.OK) { // registration was successful
            response.getWriter().println("Logged in: " + user);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            //setting session to expiry in 30 mins
            session.setMaxInactiveInterval(30 * 60);
            response.sendRedirect("/HotelsServlet");
        } else if (status == Status.MISSING_VALUES) {
            response.getWriter().println("Missing UserName or Password or Both values");
        } else if (status == Status.INVALID_LOGIN) {

            response.getWriter().println("Invalid Password");
        } else if (status == Status.CONNECTION_FAILED) {

            response.getWriter().println("Wrong Credentials");
        } else {
            response.getWriter().println("Please check all details again");
        }
    }
}