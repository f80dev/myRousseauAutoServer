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
import com.google.common.hash.Hashing;
import com.googlecode.objectify.annotation.*;
import com.restfb.*;
import com.sugaronrest.modules.Contacts;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Cache
@Entity
public class User {
    public static final String ADMIN_EMAIL = "rv@f80.fr";
    //public static final String TITLE_APPLI = "My Rousseau Automobile";
    public static final String TITLE_APPLI = "SelfApp";
    //public static final String CRM_DOMAIN = "http://172.17.242.201"; //en local
    public static final String CRM_DOMAIN = "https://server.f80.fr"; //distant
    public static final String CRM_USER = "selfapp";
    public static final String CRM_PASSWORD = "hh4271";

    @JsonIgnore
    @Ignore
    private FacebookClient facebook = null;

    @Id
    String email = "";

    @Index
    String id = null;

    @Index
    String crm_contactsID="";

    private List<Long> connexions = new ArrayList<>();
    String lang = "";
    String lastname = "";
    String firstname = "";
    String phone="";
    Boolean shareProfil=false;

    Long dtLastNotif=100000000000L;

    Integer pts = 100;
    //String password = ""+System.currentTimeMillis() % 9999;
    String password = "1234";
    String photo = "./assets/img/avatar.jpg";

    public Map<String, token> accessTokens = new HashMap<>();

    HashMap<String, Long> gifts = new HashMap<>(); //Liste des cadeaux attribués

    List<String> products = new ArrayList<>();

    List<String> messagesReaded = new ArrayList<>();

    public User() {
    }

    public User(String email, String password, String firstname, String lastname) {
        this.setEmail(email);
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
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
        this.id = Hashing.sha256().hashString(email, StandardCharsets.UTF_8).toString();
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean getShareProfil() {
        return shareProfil;
    }

    public void setShareProfil(Boolean shareProfil) {
        this.shareProfil = shareProfil;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Long getDtLastNotif() {
        return dtLastNotif;
    }

    public List<String> getMessagesReaded() {
        return messagesReaded;
    }

    public void setMessagesReaded(List<String> messagesReaded) {
        this.messagesReaded = messagesReaded;
    }

    public void setDtLastNotif(Long dtLastNotif) {
        this.dtLastNotif = dtLastNotif;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Long> getConnexions() {
        return connexions;
    }

    public void setConnexions(List<Long> connexions) {
        this.connexions = connexions;
    }

    public void addConnexion() {
        this.connexions.add(System.currentTimeMillis());
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

//    public void setCars(List<car> cars) {
//        this.cars = cars;
//    }
//
//    public void addCar(car c) {
//        if (c.isValid())
//            this.cars.add(c);
//    }
//
//    public void delCar(Integer index) {
//        if (index < this.cars.size())
//            this.cars.remove(this.cars.get(index));
//    }

    public void setproducts(List<String> products) {
        this.products = products;
    }

    public void addproduct(Product c) {
        if (c.isValid())
            this.products.add(c.id);
    }

    public void delproduct(Integer index) {
        if (index < this.products.size())
            this.products.remove(this.products.get(index));
    }

    

    public void addGift(String gift) {
        this.gifts.put(gift, System.currentTimeMillis());
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
                "code=" + this.getPassword(),
                "firstname=" + this.getFirstname(),
                "titre=Rousseau Automobile",
                "url_to_connect=" + Tools.getDomainAppli() + "/login?email=" + this.getEmail() + "&password=" + this.getPassword());

        Tools.sendMail(this.getEmail(), "Votre code est le " + this.getPassword(), ADMIN_EMAIL, "code", params);
    }

    public boolean contain(Gift g) {
        return (this.gifts.containsKey(g.Id));
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCrm_contactsID() {
        return crm_contactsID;
    }

    public void setCrm_contactsID(String crm_contactsID) {
        this.crm_contactsID = crm_contactsID;
    }

    public Map<String, token> getAccessTokens() {
        return accessTokens;
    }

    public void setAccessTokens(Map<String, token> accessTokens) {
        this.accessTokens = accessTokens;
    }

    public Boolean updateIdentity(String service_name) throws IOException, RestAPIException {
        if (service_name.equals("contact")) {
            Person me = getPeopleService().people().get("people/me").setPersonFields("locales,names,emailAddresses,photos").execute();
            if (this.firstname.length() == 0 && me.getNames().size() > 0)
                this.firstname = me.getNames().get(0).getDisplayName().split(" ")[0];
            if (me.getEmailAddresses().size() > 0 && this.getEmail().length() == 0)
                this.email = me.getEmailAddresses().get(0).getValue();
            if (me.getLocales().size() > 0) {
                com.google.api.services.people.v1.model.Locale local = me.getLocales().get(0);
                this.setLang(local.getValue());
            }
            if (me.getPhotos().size() > 0) {
                String url = me.getPhotos().get(0).getUrl();
                this.setPhoto(url);
            }
        }

        if(service_name.startsWith("facebook")) {
            this.facebook=new DefaultFacebookClient(getAccessTokens().get("facebook").getToken(),new DefaultWebRequestor(),new DefaultJsonMapper(), Version.VERSION_2_8);
            com.restfb.types.User fbUser = this.facebook.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", "email,picture,first_name"));
            this.setEmail(fbUser.getEmail());
            this.setFirstname(fbUser.getFirstName());
            this.setPhoto(fbUser.getPicture().getUrl());
        }


        if (service_name.equals("linkedin")) {
            token service = this.accessTokens.get(service_name);
            String s = service.getEndpoint() + "/me?projection=(firstName,lastName,profilePicture)";
            JsonNode u = Tools.toJSON(Tools.rest(s,null,"Bearer "+service.getToken(),"application/json","GET"));
            s=service.getEndpoint()+"/emailAddress?q=members&projection=(elements*(handle~))";
            JsonNode u2 = Tools.toJSON(Tools.rest(s,null,"Bearer "+service.getToken(),"application/json","GET"));
            this.initUserFromLinkedin(u);
            this.email=u2.get("elements").get(0).get("handle~").get("emailAddress").asText();
        }

        return true;
    }

    private void initUserFromLinkedin(JsonNode u) {
        this.firstname = u.get("firstName").get("localized").get("fr_FR").asText();
        this.lastname= u.get("lastName").get("localized").get("fr_FR").asText();
        if (u.has("emailAddress") && this.getEmail().length() == 0) this.setEmail(u.get("emailAddress").asText());
    }


    @JsonIgnore
    protected PeopleService getPeopleService() {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory).build().setAccessToken(this.accessTokens.get("contact").getToken());

        PeopleService rc = new PeopleService.Builder(httpTransport, jsonFactory, credential).setApplicationName("rousseauAuto").build();
        return rc;
    }

    public Long setAccessTokens(String service, String redirect_uri, String code, JsonNode secret) throws IOException, RestAPIException {
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

        token tok = null;
        if (accessToken == null)
            tok = new token(res, endpoint, secretid, appid);
        else
            tok = new token(accessToken, expire_date, refreshToken, endpoint, secretid, appid);

        //L'obtention des token est agnostic au coté local
        //service=service.replace("_local","");

        tok.setIdUser(idUser);
        this.getAccessTokens().put(service, tok);

        //Retraitement des données
        if (service.equals("facebook") || service.equals("fb_local") || service.equals("linkedin")) {
            tok.setDtExpire(tok.getDelayExpire() * 1000 + System.currentTimeMillis());
        }

        return tok.getDtExpire();
    }


    public void addPoints(int i) {
        this.pts += i;
    }
    

    public Boolean updateCRM() {
        return SuiteCRM.updateCRM(this);
    }

    public Contacts toContact(){
        Contacts c=new Contacts();
        c.setLastName(this.getLastname());
        c.setId(this.getCrm_contactsID());
        c.setDateModified(new Date());
        c.setFirstName(this.getFirstname());
        return c;
    }


    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    @OnSave
    void onSave(){
        //this.updateCRM();
    }

    public boolean alreadyView(String messageId) {
        return this.messagesReaded.contains(messageId);
    }

    public boolean addReadedMessage(String messageId){
        if(alreadyView(messageId))return false;

        this.messagesReaded.add(messageId);
        return true;
    }
}
