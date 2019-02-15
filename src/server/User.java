package server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.common.io.BaseEncoding;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;


@Entity
public class User {
    public static final String ADMIN_EMAIL = "rv@f80.fr";
    @Id
    String email="";
    String lang="";
    String lastname="";
    String firstname="";
    Integer pts =100;
    String password="1234";
    String photo="./assets/img/avatar.jpg";
    public Map<String, token> accessTokens = new HashMap<>();

    HashMap<String,Long> gifts= new HashMap<>(); //Liste des cadeaux attribués

    List<car> cars=new ArrayList<>();

    public User() {
    }

    public User(String email, String password,String firstname, String lastname){
        this.email=email;
        this.firstname=firstname;
        this.lastname=lastname;
        this.password=password;
    }

    public Integer getPts() {
        return pts;
    }

    public void setPts(Integer pts) {
        this.pts = pts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public List<car> getCars() {
        return cars;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public HashMap<String, Long> getGifts() {
        return gifts;
    }

    public void setGifts(HashMap<String, Long> gifts) {
        this.gifts = gifts;
    }

    public void setCars(List<car> cars) {
        this.cars = cars;
    }

    public void addCar(car c) {
        this.cars.add(c);
    }


    public void addGift(String gift) {
        this.gifts.put(gift,System.currentTimeMillis());
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    public void sendPassword() {
        List<String> params = Arrays.asList(
                "code="+this.getPassword(),
                "firstname="+this.getFirstname(),
                "titre=Rousseau Automobile",
                "url_to_connect="+Tools.getDomainAppli()+"/login?email="+this.getEmail()+"&password="+this.getPassword());
        Tools.sendMail(this.getEmail(),"Votre code",ADMIN_EMAIL,"code",params);
    }

    public boolean contain(Gift g) {
        return(this.gifts.containsKey(g.Id));
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Map<String, token> getAccessTokens() {
        return accessTokens;
    }

    public void setAccessTokens(Map<String, token> accessTokens) {
        this.accessTokens = accessTokens;
    }

    public Boolean updateIdentity(String service_name) throws IOException, RestAPIException {
        if(service_name.equals("contact")){
            Person me = getPeopleService().people().get("people/me").setPersonFields("locales,names,emailAddresses,photos").execute();
            if(this.firstname.length()==0 && me.getNames().size()>0)this.firstname=me.getNames().get(0).getDisplayName().split(" ")[0];
            if(me.getEmailAddresses().size()>0 && this.getEmail().length()==0)this.email=me.getEmailAddresses().get(0).getValue();
            if(me.getLocales().size()>0){
                com.google.api.services.people.v1.model.Locale local = me.getLocales().get(0);
                this.setLang(local.getValue());
            }
            if(me.getPhotos().size()>0){
                String url=me.getPhotos().get(0).getUrl();
                this.setPhoto(url);
            }
        }

        return true;
    }

    @JsonIgnore
    protected PeopleService getPeopleService(){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory).build().setAccessToken(this.accessTokens.get("contact").getToken());

        PeopleService rc = new PeopleService.Builder(httpTransport, jsonFactory, credential).setApplicationName("rousseauAuto").build();
        return rc;
    }

    public Long setAccessTokens(String service, String redirect_uri, String code,JsonNode secret) throws IOException, RestAPIException {
        String accessToken = null;
        String refreshToken = null;
        Long expire_date = 0L;
        String idUser = "";
        redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
        JsonFactory factory = new ObjectMapper().getFactory();
        String res = "";

        String appid = secret.get("appid").asText();
        String secretid = secret.get("secretid").asText();
        String endpoint = secret.get("endpoint").asText();


        //https://developer.linkedin.com/docs/oauth2
        if (service.equals("linkedin")) {
            res = Tools.rest("https://www.linkedin.com/oauth/v2/accessToken",
                    "code=" + code + "&client_id=" + appid + "&client_secret=" + secretid + "&redirect_uri=" + redirect_uri + "&grant_type=authorization_code",
                    null, "application/x-www-form-urlencoded", "post");
        }



        if (service.equals("facebook") || service.equals("fb_local")) {
            res = Tools.rest("https://graph.facebook.com/v2.9/oauth/access_token", "code=" + code + "&client_id=" + appid + "&client_secret=" + secretid + "&redirect_uri=" + redirect_uri + "&grant_type=authorization_code", null, null, "post");
        }


        //https://developers.google.com/identity/protocols/OAuth2WebServer
        if (service.equals("contact") || service.equals("drive") || service.equals("calendar")) {
            String body = "code=" + code + "&client_id=" + appid + "&client_secret=" + secretid + "&redirect_uri=" + redirect_uri + "&grant_type=authorization_code";
            res = Tools.rest(endpoint, body, null, null, "post");
        }

         token tok=null;
        if(accessToken==null)
            tok=new token(res,endpoint,secretid,appid);
        else
            tok=new token(accessToken,expire_date,refreshToken,endpoint,secretid,appid);

        //L'obtention des token est agnostic au coté local
        //service=service.replace("_local","");

        tok.setIdUser(idUser);
        this.getAccessTokens().put(service,tok);

        //Retraitement des données
        if(service.equals("facebook") || service.equals("fb_local") || service.equals("linkedin")){
            tok.setDtExpire(tok.getDelayExpire()*1000+System.currentTimeMillis());
        }

        return tok.getDtExpire();
    }


    public void addPoints(int i) {
        this.pts+=i;
    }
}
