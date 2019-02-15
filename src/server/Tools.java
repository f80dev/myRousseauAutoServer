package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import com.google.common.io.CharStreams;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Tools {

    private static final Logger log = Logger.getLogger(Tools.class.getName());
    private static String domainAppli="http://localhost:4200";

    public static HttpURLConnection buildConnection(String link,String body,String authorization,String contentType,String method,Integer delayInSec) throws IOException {
        URL url=null;
        if(method==null)method="GET";else method=method.toUpperCase();
        if(!link.startsWith("http"))link="http://"+link;

        log.info("Appel de "+link+" avec body="+body);

        url= new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(delayInSec*1000);

        if(contentType==null)
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        else
            conn.setRequestProperty( "Content-Type", contentType);

        conn.setRequestProperty("User-Agent","ShifumixApp/3.0");

        if(authorization!=null)conn.setRequestProperty("Authorization", authorization);

        conn.setRequestMethod(method.toUpperCase());
        if(body!=null){
            byte[] postData= body.getBytes("utf-8");
            int postDataLength = postData.length;
            conn.setDoOutput(true);
            conn.setRequestProperty( "accept", "application/json");
            conn.setRequestProperty( "accept-charset", "UTF-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try(DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {wr.write( postData );}
        }

        return conn;

    }


    public static String rest(String link,String body,String authorization,String contentType,String method,Integer delayInSec) throws RestAPIException {

        try {
            int respCode=0;

            HttpURLConnection conn=buildConnection(link, body, authorization, contentType, method, delayInSec);
            if(respCode==0){
                respCode = conn.getResponseCode();  // New items get NOT_FOUND on PUT
            }

            if(respCode>=400) {
                log.severe("Anomalie grave avec " + link + " body=" + body + " authorisation=" + authorization);
                log.severe("Code reponse:" + respCode + " message:" + conn.getResponseMessage());
                throw new RestAPIException("ResponseCode" + respCode);
            } else {
                StringBuffer response = new StringBuffer();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
                while ((line = reader.readLine()) != null){
                    if(line.length()>0)line=line+"\n";
                    response.append(line);
                }

                reader.close();

                String s=String.valueOf(response);
                log.info("réponse:"+s.substring(0,Math.min(50,s.length())));
                return String.valueOf(response);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.severe(e.getMessage()+" pour "+link);
        }

        return null;
    }

    public static JsonNode getService(Class c, String service) {
        BufferedInputStream result = (BufferedInputStream) c.getClassLoader().getResourceAsStream("services.json");
        try {
            String s= CharStreams.toString(new InputStreamReader(result));
            Iterator<JsonNode> ite = toJSON(s).get("services").elements();
            while(ite.hasNext()){
                JsonNode rc=ite.next();
                if(rc.get("name").asText().equals(service))return rc;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode toJSON(String s){
        if(s==null){
            return null;
        }
        s=s.replaceAll("\n","");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(s.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDomain(){
        String rc="https://"+System.getProperty("application")+".appspot.com:443";
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
            return rc;
        } else
            return "http://localhost:8080";
    }



    public static String rest(String link) throws RestAPIException {
        return rest(link,null,null,null,"GET",30);
    }

    public static String rest(String link,String body,String authorization,String contentType,String method) throws RestAPIException {
        return rest(link,body,authorization,contentType,method,30);
    }


    public static String getDate(Long dt) {
        if(dt==null)dt=new Date().getTime();
        GregorianCalendar calendar = new GregorianCalendar();
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRANCE);
        TimeZone timeZone = TimeZone.getTimeZone("CST");
        formatter.setTimeZone(timeZone);

        return formatter.format(dt);
    }


    public static Boolean sendMailByGoogle(String dest,String subject,String from,String body){
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            javax.mail.Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(dest));
            msg.setSubject(subject);
            msg.setContent(body,"text/html; charset=utf-8");
            Transport.send(msg);
            return true;
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (ApiProxy.OverQuotaException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String replacePattern(String s,List<String> params){
        if(params!=null){

            for(String rep:params){
                int pos=rep.indexOf("=");
                if(pos>-1){
                    String field=rep.substring(0,pos);
                    String new_value=rep.substring(pos+1);
                    int i=0,p=0;
                    do{
                        p=i;
                        i=s.indexOf("@"+field+"@",p);
                        if(i==-1)i=s.indexOf("%40"+field+"%40",p);
                        if(i>-1){
                            s=s.substring(0,i)+new_value+s.substring(i+field.length()+2);
                            p=i;
                        }
                    }while(i>-1);
                }
            }
        }

        return s;
    }


    public static void sendMail(String dest,String subject,String from,String template,List<String> params){
        String s= null;
        try {
            s = Tools.rest(Tools.getDomain()+"/templates/"+template+".html");
        } catch (RestAPIException e) {
            e.printStackTrace();
        }
        String html=replacePattern(s,params);
        sendMailByGoogle(dest,subject,from,html);
    }


    public static  String getDomainAppli() {
        return domainAppli;
    }

    public static void setDomainAppli(String domAppli) {
        domainAppli=domAppli;
    }


    public static HashMap<String, String> returnAPI(Integer errorCode, String userMessage, String internMessage) {
        HashMap<String, String> rc=new HashMap<>();

        if(errorCode==null)errorCode=200;
        rc.put("code", String.valueOf(errorCode));

        if(userMessage==null)userMessage="";
        rc.put("message",userMessage);
        if(internMessage!=null){
            rc.put("log",internMessage);
            log.warning(internMessage);
        }
        return rc;
    }



}

