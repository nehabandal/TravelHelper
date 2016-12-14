package cs601.travelHelper;

import org.apache.velocity.tools.view.VelocityViewServlet;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides base functionality to all servlets in this example. Original author:
 * Prof. Engle
 *
 * @see RegisterServer
 */
@SuppressWarnings("serial")
public class BaseServlet extends VelocityViewServlet {

    protected void prepareResponse(String title, HttpServletResponse response) {

    }

    protected void finishResponse(HttpServletResponse response) {

    }

    protected String getDate() {
        String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Return a cookie map from the cookies in the request
     *
     * @param request
     * @return
     */
    protected Map<String, String> getCookieMap(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<String, String>();

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }

        return map;
    }

    /**
     * Clear cookies
     *
     * @param request
     * @param response
     */
    protected void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    protected void clearCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    protected String getStatusMessage(String errorName) {
        Status status = null;

        try {
            status = Status.valueOf(errorName);
        } catch (Exception ex) {
            status = Status.ERROR;
        }

        return status.toString();
    }

    protected String getStatusMessage(int code) {
        Status status = null;

        try {
            status = Status.values()[code];
        } catch (Exception ex) {
            status = Status.ERROR;
        }

        return status.toString();
    }

    /**
     * checking login session for user, if user is not logged in then go back to login page
     *
     * @param request
     * @param response
     * @throws IOException
     */
    protected void checkLoginState(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect("/LoginServlet");
        }

    }

}