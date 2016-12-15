package cs601.travelHelper;

import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by npbandal on 12/13/16.
 */
public abstract class RestApiServlet extends BaseServlet {
    protected DatabaseConnector db;

    public RestApiServlet() {
        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            returnErrorJson(response, e.getMessage());
        }
    }

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    /**
     * Method to fetch JSON error everytime
     * @param response
     * @param message
     * @throws IOException
     */
    private void returnErrorJson(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", message);
        out.print(jsonObject);
        out.flush();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Method to send successful response
     * @param response
     * @param results
     * @throws IOException
     */
    protected void resultJson(HttpServletResponse response, Map<String, String> results) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> e : results.entrySet()) {
            jsonObject.put(e.getKey(), e.getValue());
        }
        out.print(jsonObject);
        out.flush();
    }

}
