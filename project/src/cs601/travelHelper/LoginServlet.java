package cs601.travelHelper;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * A servlet that handles user registration. doGet() method displays an HTML form with a button and
 * two textfields: one for the username, one for the password.
 * doPost() processes the form: if the username is not taken, it adds user info to the database.
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {

    // DatabaseHandler interacts with the MySQL database
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

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
        }
        else if(status==Status.MISSING_VALUES)
        {
            response.getWriter().println("Missing UserName or Password or Both values");
        }
        else if(status==Status.INVALID_LOGIN){

            response.getWriter().println("Invalid Password");
        }
        else if(status==Status.CONNECTION_FAILED){

            response.getWriter().println("Wrong Credentials");
        }
        else
        {
            response.getWriter().println("Please check all details again");
        }
    }
}