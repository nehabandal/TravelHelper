package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

	public List<HotelList> list() throws SQLException {
		List<HotelList> reviews = new ArrayList<HotelList>();

		try (
				Connection connection = db.getConnection();
				PreparedStatement statement = connection.prepareStatement("select hotelData.hotelName,hotelData.address, avg(rating) as rat from hotelData LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId group by(hotelData.hotelId)");
				ResultSet resultSet = statement.executeQuery();
		) {
			while (resultSet.next()) {
				HotelList hotels = new HotelList();
				hotels.setHotelName(resultSet.getString("hotelName"));
				hotels.setAddress(resultSet.getString("address"));
				hotels.setRating(resultSet.getDouble("rat"));

				reviews.add(hotels);
			}
		}

		return reviews;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		db = new DatabaseConnector("database.properties");
int count=0;

		out.println("<html><body>");
		try {Connection connection = db.getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select hotelData.hotelId,hotelData.hotelName,hotelData.address,hotelData.city, avg(rating) as rat from hotelData LEFT JOIN reviewData on hotelData.hotelId=reviewData.hotelId group by(hotelData.hotelId)");
			ArrayList h=new ArrayList();

			out.println("<table border=1 width=50% height=50%>");
			out.println("<tr><th>HotelName</th><th>Address</th><th>City</th><th>Rating</th><tr>");

            while (rs.next ()){
				HotelList e = new HotelList();
				int id=rs.getInt("hotelId");
				String n= rs.getString("hotelName");
                String nm = rs.getString("address");
				String City=rs.getString("city");
                double s = rs.getDouble("rat");
h.add(n);
				out.println("<tr><td>" +"<a href=\"reviews.html\">"+n+"</a>"  +"</td><td>" + nm +"</td><td>" + City+"</td><td>" + s +"</td></tr>");
				//out.println("<script>"+"function hotels() {"+"document.getElementById("+"demo"+").innerHTML="+ session.setAttribute("hotelNames",n)+";}"+"</script>");
	session.setAttribute("id",id);


//<p id="demo"></p>
            }


            out.println("<a href=\"logout.html\">Logout</a>");
            out.println("</table>");
            out.println("</html></body>");


//            //setting session to expiry in 30 mins
//			request.setAttribute("results", ee);
//			RequestDispatcher rd = request.getRequestDispatcher("reviews.html");
//			rd.forward(request, response);


			connection.close();
		}
		catch (Exception e) {
			out.println("error");
		}





	}

}