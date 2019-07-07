package server;
import com.fasterxml.jackson.databind.JsonNode;
import com.sugaronrest.modules.Prospects;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by u016272 on 25/12/2016.
 */
public class OAuth2Callback extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final DAO dao = DAO.getInstance();
    private static final Logger log = Logger.getLogger(Rest.class.getName());


    //exemple d'accessToken=fr48x45178ydHi6ZcIlVkn5vXoXQuJkheNItbfQeBS8dxf1TEL4
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code=req.getParameter("code");
        String server=req.getParameter("from");
        String state=req.getParameter("state");
        User u=new User();
        String redirect_uri=req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/api/oauth2callback?from="+server;
        redirect_uri=redirect_uri.replace(":/o","/o");

        if(code!=null && u!=null) {
            JsonNode jsonnode = Tools.getService(this.getClass(), server);
          try {
            resp.addHeader("X-FRAME-OPTIONS", "ALLOW-FROM *");
            u.setAccessTokens(server,redirect_uri,code,jsonnode);

            if (u.updateIdentity(server)) {
              if(state.indexOf("localhost")>-1)
                  state="http://localhost:4200";
              else{
                  state=state.replace("_slash_","/");
                  if(!state.startsWith("https"))state="https://"+state;
              }


                if(dao.get(u.getEmail())==null){
                    u.sendPassword();
                    //SuiteCRM.createFromUser(u, Prospects.class);
                    dao.save(u);
                } else {
                    u=dao.get(u.getEmail());
                }

                resp.sendRedirect(state+"/channel.html?email=" + u.getEmail()+"&password="+u.getPassword());

            }

          } catch (RestAPIException e) {
            e.printStackTrace();
            resp.sendRedirect("/error.html?message=Service+not+responding");
          }
        }

    }
}
