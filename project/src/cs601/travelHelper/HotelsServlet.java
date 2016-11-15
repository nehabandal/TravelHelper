package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Demonstrates how to use the HttpSession class to keep track of the number of visits for each client 
 * (and the date of the last visit).
 * Modified from the example of Prof. Rollins.
 *
 */

@SuppressWarnings("serial")
public class HotelsServlet extends BaseServlet {


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


		String title = "Session Servlet";
		String header = "<!DOCTYPE html\n>" + "	<head>\n" + "<title>" + title + "</title>\n" + "</head>\n";

		String body = "	<body>\n" +  "<p>Hello " + user + "! You have visited " + visitCount + " time(s).</p>\n"+
				"<a href=\"logout.html\">Logout</a>";

		if (visitDate != null) {
			body = body + "<p> Your last visit was on " + visitDate + "</p>\n";
		}

		body = body + "	</body>\n";
		String footer = "</html>";

		String page = header + body + footer;
		out.println(page);
		String format = "yyyy-MM-dd hh:mm a";
		DateFormat formatter = new SimpleDateFormat(format);
		visitDate = formatter.format(Calendar.getInstance().getTime());
		session.setAttribute("date", visitDate);


	}
/*	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response);

		// Get data from the textfields of the html form
		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		// sanitize user input to avoid XSS attacks:
		newuser = StringEscapeUtils.escapeHtml4(newuser);
		newpass = StringEscapeUtils.escapeHtml4(newpass);

		// add user's info to the database
		//Status status = dbhandler.addHotelDB()

//		if(status == Status.OK) { // registration was successful
//			response.getWriter().println("Registered! Database updated.");
//		}
		else { // if something went wrong
			String url = "/register?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url);
			// send a get request  (redirect to the same path)
			response.getWriter().println("no connection");
		}
	}*/

}