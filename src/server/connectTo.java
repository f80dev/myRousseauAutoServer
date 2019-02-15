package server;

import com.fasterxml.jackson.databind.JsonNode;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;


/**
 * Created by u016272 on 25/12/2016.
 */
public class connectTo extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    JsonNode service = Tools.getService(this.getClass(), req.getParameter("service"));

    String from=service.get("token").asText().replace("\"","");
    String urlcallback=Tools.getDomain()+"/oauth2callback?from="+from;
    String appid=service.get("appid").asText().replace("\"","");
    String scope=service.get("scope").asText().replace("\"","");

    String secretid_name="app_id";
    if(service.has("secretid_name"))secretid_name=service.get("secretid_name").asText();
    String url=service.get("authpoint").asText()
                 +"?client_id="+appid
                 +"&"+secretid_name+"="+appid
                 +"&redirect_uri="+ URLEncoder.encode(urlcallback,"UTF-8")
                 +"&access_type=offline"
                 +"&scope="+URLEncoder.encode(scope,"UTF-8")
                 +"&perms="+URLEncoder.encode(scope,"UTF-8")
                 +"&response_type=code"
                 +"&state="+ req.getParameter("domain");

    resp.addHeader("X-FRAME-OPTIONS", "ALLOW-FROM *");
    resp.sendRedirect(url);
  }
}
