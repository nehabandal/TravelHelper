package cs601.travelHelper;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A servlet that handles user registration. doGet() method displays an HTML form with a button and
 * two textfields: one for the username, one for the password.
 * doPost() processes the form: if the username is not taken, it adds user info to the database.
 *
 */
@SuppressWarnings("serial")
public class RegisterServlet extends BaseServlet {

    // DatabaseHandler interacts with the MySQL database
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        prepareResponse("Register New User", response);

        PrintWriter out = response.getWriter();

        // error will not be null if we were forwarded her from the post method where something went wrong
        String error = request.getParameter("error");
        if(error != null) {
            String errorMessage = getStatusMessage(error);
            out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
        }


        finishResponse(response);
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

        Status status=  dbhandler.registerUser(newuser, newpass);
        if(status == Status.OK) { // registration was successful
            response.getWriter().println("Registered! Database updated.");
        }
        else if(status==Status.DUPLICATE_USER)
        {
            response.getWriter().println("User already exist");
        }
        else if(status==Status.INVALID_LOGIN)
        {
            response.getWriter().println("Username and password can't be empty");
        }
        else if(status==Status.MISSING_VALUES)
        {
            response.getWriter().println("Password length is less than 8");
        }
        else if(status==Status.ERROR)
        {
            response.getWriter().println("Password should contain at least one special character");
        }

        else {
            response.getWriter().println("Please recheck login");
        }
        //response.sendRedirect("login.html");
    }


}