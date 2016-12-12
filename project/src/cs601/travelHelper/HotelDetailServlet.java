package cs601.travelHelper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 11/15/16.
 */
public class HotelDetailServlet extends BaseServlet {
    public static final String REVIEW_QUERY_SQL = "select reviewTitle,review,username,rating,date from reviewData where hotelId= ? order by date";
    public static final String ATTRACTION_QUERY_SQL = "select attractionName,rating,address from attractionData where hotelId= ? order by rating";
    private DatabaseConnector db;

    public HotelDetailServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Template handleRequest(HttpServletRequest request,
                                  HttpServletResponse response, Context context) {
        try {
            checkLoginState(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpSession session = request.getSession();
        int hotelId = Integer.parseInt(request.getParameter("hotelId"));
        session.setAttribute("hotelId", hotelId);

        String hotelName = request.getParameter("hotelName");
        //session.setAttribute("hotelName", hotelName);

        prepareResponse("review",response);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext vc = new VelocityContext();
        Template template = ve.getTemplate("web/templates/hotel_detail.vm");
        PrintWriter out=null;
        try {
            out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.put("hotelName", hotelName);
        //context.put("reviews", getReviewRows(hotelId));
        //context.put("attractions", getAttractionRows(hotelId));

        finishResponse(response);
        return template;
    }
}
