package eu.emrex;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "EMPList", urlPatterns = { "/emreg/list", "/emreg/list/*" })
public class EMPListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(EMPListServlet.class);


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpURLConnection conn = null;
        String json = null;
        try {
            String env = "";
            String pathInfo = request.getPathInfo();
            if (pathInfo != null) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts != null) {
                    env = "/" + pathParts[1];
                }
            }

            conn = Util.setupConnection(System.getProperty("emrex.emreg_url") + env, "GET");
            response.setStatus(conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                json = Util.getDataFromConnection(conn);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(json);
            }
        } catch (IOException t) {
            logger.error("Failed to get EMP list.", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Failed to get EMP list: " + t.getMessage());
        }

    }

}
