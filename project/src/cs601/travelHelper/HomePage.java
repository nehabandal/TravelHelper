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
public class HomePage extends BaseServlet {

    // DatabaseHandler interacts with the MySQL database
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    BaseServlet bs=new BaseServlet();
    public Template handleRequest(HttpServletRequest request,
                                  HttpServletResponse response, Context context) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            try {
                response.sendRedirect("/HotelsServlet");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        prepareResponse("home",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/HomePage.vm");
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


}