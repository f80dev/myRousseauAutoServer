package server;
import com.fasterxml.jackson.databind.JsonNode;
import com.sugaronrest.modules.Prospects;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Created by u016272 on 25/12/2016.
 */
public class export extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final DAO dao = DAO.getInstance();
    private static final Logger log = Logger.getLogger(export.class.getName());


    //exemple http://localhost:8080/api/export
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
        try {
            OutputStream outputStream = resp.getOutputStream();
            outputStream.write(dao.exportWorksToCSV().getBytes());
            outputStream.flush();
            outputStream.close();
        }   catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
}
