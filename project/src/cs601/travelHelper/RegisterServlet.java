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
public class RegisterServlet extends BaseServlet {

    // DatabaseHandler interacts with the MySQL database
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    private final String innerHtml =
            "\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Register New User</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<form action=\"/RegisterServlet\" method=\"post\">\n" +
                    "    <table border=\"0\">\n" +
                    "        <tr>\n" +
                    "            <td>Usename:</td>\n" +
                    "            <td><input type=\"text\" name=\"user\" size=\"30\"></td>\n" +
                    "        </tr>\n" +
                    "        <tr>\n" +
                    "            <td>Password:</td>\n" +
                    "            <td><input type=\"password\" name=\"pass\" size=\"30\"></td>\n" +
                    "        </tr>\n" +
                    "    </table>\n" +
                    "    <p><input type=\"submit\" value=\"Register\"></p>\n" +
                    "</form>\n" +
                    "</body>\n" +
                    "</html>\n";


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            response.sendRedirect("/HotelsServlet");
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        out.println(innerHtml);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        PrintWriter out = response.getWriter();
        prepareResponse("Register New User", response);

        // Get data from the textfields of the html form
        String newuser = request.getParameter("user");
        String newpass = request.getParameter("pass");
        // sanitize user input to avoid XSS attacks:
        newuser = StringEscapeUtils.escapeHtml4(newuser);
        newpass = StringEscapeUtils.escapeHtml4(newpass);

        Status status = dbhandler.registerUser(newuser, newpass);
        if (status == Status.OK) { // registration was successful
            response.getWriter().println("Registered! Database updated.");
            response.sendRedirect("/LoginServlet");
        } else if (status == Status.DUPLICATE_USER) {
            response.getWriter().println("User already exist");
        } else if (status == Status.INVALID_LOGIN) {
            response.getWriter().println("Username and password can't be empty");
        } else if (status == Status.MISSING_VALUES) {
            response.getWriter().println("Password length is less than 8");
        } else if (status == Status.ERROR) {
            response.getWriter().println("Password should contain at least one special character");
        } else if (status == Status.INVALID_USER) {
            response.getWriter().println("Special case letters are not allowed for user name");
        } else {
            response.getWriter().println("Please recheck login");
        }
    }


}