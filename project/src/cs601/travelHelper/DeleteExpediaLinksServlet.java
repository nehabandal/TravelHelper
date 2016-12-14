package cs601.travelHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by npbandal on 12/13/16.
 */
public class DeleteExpediaLinksServlet extends RestApiServlet {
    final String DELETE_EXPEDIA_LINKS_SQL = "DELETE FROM expediaLinks WHERE user = ?;";

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            checkLoginState(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("user");

        int result = executeUpdate(user);

        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("#ExecuteUpdate return value: " + result);
        response.sendRedirect("/ShowExpediaLinksServlet");
    }

    private int executeUpdate(String user) throws SQLException {
        Connection connection = db.getConnection();

        PreparedStatement stmt = connection.prepareStatement(DELETE_EXPEDIA_LINKS_SQL);
        stmt.setString(1, String.valueOf(user));

        int result = stmt.executeUpdate();
        connection.close();
        return result;
    }

}
