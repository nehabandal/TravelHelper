package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by npbandal on 11/15/16.
 */
public class ReviewsServlet extends BaseServlet {
    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    private static DatabaseConnector db;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(true);
       //int id= (int) session.getAttribute("id");
        int id=17280;
       // String hotelnames="1800 Powell Street";
        PrintWriter out = response.getWriter();
        out.println(id);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
        
       

      db = new DatabaseConnector("database.properties");

        out.println("<html><body>");
        try {Connection connection = db.getConnection();

            PreparedStatement stmt = connection.prepareStatement("select reviewTitle,review,username,rating from reviewData where hotelId= ?");
            stmt.setString(1, String.valueOf(id));
            ResultSet rs = stmt.executeQuery();
            out.println("<table border=1 width=50% height=50%>");
            out.println("<tr><th>ReviewTitle</th><th>Review</th><th>Username</th><th>Rating</th><tr>");
            while (rs.next()) {
                String reviewTitle = rs.getString("reviewTitle");
                String review=rs.getString("review");
                String username=rs.getString("username");
                double rating=rs.getDouble("rating");

                out.println("<tr><td>"+reviewTitle+"</td><td>"+review+"</td><td>"+username+"</td><td>"+rating+"</td></tr>");
            //  +"</td><td>"  + "</td><td>" + "</td></tr>");
            }
           // response.sendRedirect();
            out.println("<a href=\"logout.html\">Logout</a>");
            out.println("</table>");
            out.println("</html></body>");
            connection.close();
        }
        catch (Exception e) {
            out.println("error");
        }





   }

}
