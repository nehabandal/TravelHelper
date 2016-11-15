package cs601.travelHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
public class LogoutServlet extends HttpServlet {

		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html");
			PrintWriter out=response.getWriter();

			HttpSession session=request.getSession();
			session.invalidate();
			
			out.print("You are successfully logged out!");
			//request.getRequestDispatcher("login.html").include(request, response);
			response.sendRedirect("login.html");
			out.close();
	}

	

}
