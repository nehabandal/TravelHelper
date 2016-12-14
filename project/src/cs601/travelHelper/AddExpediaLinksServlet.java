package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by npbandal on 12/13/16.
 */
public class AddExpediaLinksServlet extends RestApiServlet {
    public static final String INSERT_EXPEDIA_LINKS_SQL = "insert into expediaLinks (user, hotelId, url) values (?, ?, ?);";

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");
        Integer hotelId = (Integer) session.getAttribute("hotelId");
        String url = request.getParameter("url");

        addExpediaLink(username, hotelId, url);
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("url: " + url + " visited by user: " + username);

        response.sendRedirect(java.net.URLDecoder.decode(url, "UTF-8"));
    }

    private void addExpediaLink(String username, Integer hotelId, String url) throws SQLException {
        Connection connection = db.getConnection();

        PreparedStatement stmt = connection.prepareStatement(INSERT_EXPEDIA_LINKS_SQL);
        stmt.setString(1, String.valueOf(username));
        stmt.setString(2, String.valueOf(hotelId));
        stmt.setString(3, String.valueOf(url));

        int result = stmt.executeUpdate();
        System.out.println("ExecuteUpdate return value: " + result);
        connection.close();
    }

}
