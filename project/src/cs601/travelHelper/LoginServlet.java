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
public class LoginServlet extends BaseServlet {

    // DatabaseHandler interacts with the MySQL database
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    BaseServlet bs=new BaseServlet();

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
        prepareResponse("Login",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/login.vm");
        PrintWriter out=null;
        try {
            out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.put("application", "Test Application");
        //template.merge(vc,out);
        finishResponse(response);
        // context.put("header", "Velocity Sample Page");
        return template;
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
            response.sendRedirect("/WelcomePageServlet");
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