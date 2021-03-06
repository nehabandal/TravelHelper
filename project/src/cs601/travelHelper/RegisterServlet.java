package cs601.travelHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

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
    /**
     * HandleRequest
     * @param request
     * @param response
     * @param context
     * @return
     */

    public Template handleRequest(HttpServletRequest request,
                                  HttpServletResponse response, Context context) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            try {
                response.sendRedirect("/WelcomePageServlet");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        prepareResponse("Register",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/register.vm");
        PrintWriter out=null;
        try {
            out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.put("application", "Test Application");
        finishResponse(response);
        return template;

    }


    /**
     * POST method that will allow submitting user data and register new user after checking all invalidation
     * update login user table in database
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * @Override
     */

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        PrintWriter out = response.getWriter();
        prepareResponse("Register New User", response);

        // Get data from the textfields of the html form
        String newuser = request.getParameter("user");
        String newpass = request.getParameter("password");
        // sanitize user input to avoid XSS attacks:
        newuser = StringEscapeUtils.escapeHtml4(newuser);
        newpass = StringEscapeUtils.escapeHtml4(newpass);

        Status status = dbhandler.registerUser(newuser, newpass);
        if (status == Status.OK) { // registration was successful
            // response.getWriter().println("Registered! Database updated.");
            //response.sendRedirect("/WelcomePageServlet");
//            response.getWriter().println("Welcome: " + newuser);
            HttpSession session = request.getSession();
            session.setAttribute("user", newuser);
            //setting session to expiry in 30 mins
            response.sendRedirect("/WelcomePageServlet");
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