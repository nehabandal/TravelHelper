package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client 
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 *
 */

@SuppressWarnings("serial")
public class HotelsServlet extends BaseServlet {

	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	private static DatabaseConnector db;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();

		String visitDate = (String) session.getAttribute("date");
		String user = (String) session.getAttribute("user");

		Integer visitCount = (Integer) session.getAttribute("visitCount");
		if (visitCount == null)
			visitCount = 0;
		visitCount = visitCount + 1;
		session.setAttribute("visitCount", visitCount);
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		db = new DatabaseConnector("database.properties");

		out.println("<html><body>");
		try {Connection connection = db.getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT hotelName, address, rating FROM hotelData");
			out.println("<table border=1 width=50% height=50%>");
			out.println("<tr><th>HotelName</th><th>Address</th><th>Rating</th><tr>");
			while (rs.next()) {
				String n = rs.getString("hotelName");
				String nm = rs.getString("address");
				int s = rs.getInt("rating");
				out.println("<tr><td>" +"<a href=\"n\">"+n+"</a>"  +"</td><td>" + nm + "</td><td>" + s + "</td></tr>");
			}
			out.println("<a href=\"logout.html\">Logout</a>");
			out.println("</table>");
			out.println("</html></body>");
			connection.close();
		}
		catch (Exception e) {
			out.println("error");
		}


//	String title = "Session Servlet";
//		String header = "<!DOCTYPE html\n>" + "	<head>\n" + "<title>" + title + "</title>\n" + "</head>\n";
//
//		String body = "	<body>\n" +  "<p>Hello " + user + "! You have visited " + visitCount + " time(s).</p>\n"+
//				"<a href=\"logout.html\">Logout</a>"+"\t\n<a href=\"Hotellist.html\"\n> Hotels</a>";
//
//		if (visitDate != null) {
//			body = body + "<p> Your last visit was on " + visitDate + "</p>\n";
//		}
//
//		body = body + "	</body>\n";
//		String footer = "</html>";
//
//		String page = header + body + footer;
//		out.println(page);
//		String format = "yyyy-MM-dd hh:mm a";
//		DateFormat formatter = new SimpleDateFormat(format);
//		visitDate = formatter.format(Calendar.getInstance().getTime());
//		session.setAttribute("date", visitDate);



	}

}