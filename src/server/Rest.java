package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.server.spi.config.*;
import com.sugaronrest.modules.*;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.logging.Logger;

import static server.User.ADMIN_EMAIL;
import static server.User.TITLE_APPLI;


//@Api(   name = "rousseau",
//        description= "Rousseau api rest service",
//        namespace = @ApiNamespace(ownerDomain = "rousseauauto.appspot.com",ownerName = "rousseauauto.appspot.com",packagePath = ""),
//        version = "v1")
//@Api(   name = "selfapp",
//        description= "Selfapp api rest service",
//        namespace = @ApiNamespace(ownerDomain = "selfapp.appspot.com",ownerName = "selfapp.appspot.com",packagePath = ""),
//        version = "v1")

@Api(   name = "creche",
        description= "creche api rest service",
        namespace = @ApiNamespace(ownerDomain = "creche.appspot.com",ownerName = "creche.appspot.com",packagePath = ""),
        version = "v1")

public class Rest {

//    static {ObjectifyService.init();} code pour objectify 6

    private static final DAO dao = DAO.getInstance();
    private static final Logger log = Logger.getLogger(Rest.class.getName());

    @ApiMethod(name = "getuser", httpMethod = ApiMethod.HttpMethod.GET, path = "getuser")
    public User getuser(@Named("email") String user) {
        User u = dao.get(user);
        return u;
    }

    @ApiMethod(name = "getusers", httpMethod = ApiMethod.HttpMethod.GET, path = "getusers")
    public List<User> getusers() {
        List<User> rc = dao.getUsers(null);
        return rc;
    }

    @ApiMethod(name = "updateuser", httpMethod = ApiMethod.HttpMethod.POST, path = "updateuser")
    public User updateuser(@Named("email") String email,JsonNode jn) {
        User u=dao.get(email);
        if(jn.has("firstname"))u.setFirstname(jn.get("firstname").asText());
        if(jn.has("photo"))u.setPhoto(jn.get("photo").asText());
        dao.save(u);
        return u;
    }



    @ApiMethod(name = "adduser", httpMethod = ApiMethod.HttpMethod.POST, path = "adduser")
    public User adduser(@Named("dtLastNotif") Long dtLastNotif,JsonNode jn) {
        //String password=""+new Random().ints(1000,9999);
        String password="1234";
        User u=dao.get(jn.get("email").asText());
        if(u!=null){
            u.setFirstname(jn.get("firstname").asText());
            u.setLastname(jn.get("lastname").asText());
            u.setDtLastNotif(dtLastNotif);
        } else {
            u=new User(jn.get("email").asText(),password,jn.get("firstname").asText(),jn.get("lastname").asText());
            u.sendPassword();
            //SuiteCRM.createFromUser(u,Prospects.class);
        }

//        if(modele!=null && modele.length()>0){
//            car c=new car(modele,null);
//            u.addCar(c);
//        }

        dao.save(u).now();
        return u;
    }


    //http://localhost:8080/_ah/api/rousseau/v1/getcar?modele=renault_clio
    @ApiMethod(name = "getcar", httpMethod = ApiMethod.HttpMethod.GET, path = "getcar")
    public car getcar(@Named("modele") String model) {
        car c=new car(model,null);
        return c;
    }


    //http://localhost:8080/_ah/api/rousseau/v1/test
    @ApiMethod(name = "test", httpMethod = ApiMethod.HttpMethod.GET, path = "test")
    public void test(ServletContext res, @Nullable @Named("modele") String model) {
        //dao.addGifts(Tools.loadDataFile("promotions").get("gifts"));
    }

    //http://localhost:8080/_ah/api/rousseau/v1/test
    @ApiMethod(name = "getproducts", httpMethod = ApiMethod.HttpMethod.GET, path = "getproducts")
    public List<Product> getproducts(@Nullable @Named("email") String email,@Nullable @Named("id") String id) {
        if(id!=null && id.length()>0)
            return Arrays.asList(dao.getProduct(id));
        else
            return dao.getProducts(email);
    }

    //http://localhost:8080/_ah/api/rousseau/v1/test
    @ApiMethod(name = "getmessages", httpMethod = ApiMethod.HttpMethod.GET, path = "getmessages")
    public List<Message> getmessages(@Named("email") String email) {
        return dao.getMessages(dao.get(email));
    }

    //http://localhost:8080/_ah/api/rousseau/v1/test
    @ApiMethod(name = "getworks", httpMethod = ApiMethod.HttpMethod.GET, path = "getworks")
    public List<Work> getworks(@Named("productid") String productid) {
        List<Work> rc = dao.getWorks(productid);
        Collections.sort(rc);
        return rc;
    }

    @ApiMethod(name = "addwork", httpMethod = ApiMethod.HttpMethod.POST, path = "addwork")
    public void addwork(Work w) {
        Product p=dao.getProduct(w.getProduct_id());
        p.setDtStartWork(0L);
        dao.save(p);
        dao.save(w);
    }

    @ApiMethod(name = "delwork", httpMethod = ApiMethod.HttpMethod.GET, path = "delwork")
    public void delwork(@Named("work_id") String workid) {
        Work w=dao.getWork(workid);
        dao.delete(w);
    }

    //http://localhost:8080/_ah/api/rousseau/v1/test
    @ApiMethod(name = "addmessage", httpMethod = ApiMethod.HttpMethod.POST, path = "addmessage")
    public void addmessage(Message m) {
        dao.save(m);
    }

    @ApiMethod(name = "readmessage", httpMethod = ApiMethod.HttpMethod.GET, path = "readmessage")
    public void readmessage(@Named("email") String email,@Named("message") String message) {
        User u=dao.get(email);
        if(u.addReadedMessage(message))
            dao.save(u);
    }

    //http://localhost:8080/_ah/api/creche/v1/exportworks
    @ApiMethod(name = "exportworks", httpMethod = ApiMethod.HttpMethod.GET, path = "exportworks")
    public HashMap<String,String> exportworks() {
        return Tools.returnAPI(200,dao.exportWorksToCSV(),null);
    }


    //http://localhost:8080/_ah/api/rousseau/v1/getservices


    @ApiMethod(name = "getmodeles", httpMethod = ApiMethod.HttpMethod.GET, path = "getmodeles")
    public JsonNode getmodeles() {
        return Tools.loadDataFile("modeles");
    }


    @ApiMethod(name = "addproduct", httpMethod = ApiMethod.HttpMethod.POST, path = "addproduct")
    public User addproduct(@Named("email") String email, Enfant p) {
        User u=dao.get(email);
        if(u!=null){
            u.addproduct(p);
            dao.save(p);
            dao.save(u);
        }
        return u;
    }

    @ApiMethod(name = "startwork", httpMethod = ApiMethod.HttpMethod.GET, path = "startwork")
    public void startWork(@Named("product_id") String id,@Nullable @Named("dtStart") Long dtStart) {
        Product p=dao.getProduct(id);
        p.setDtStartWork(dtStart);
        dao.save(p);
    }

    @ApiMethod(name = "getservices", httpMethod = ApiMethod.HttpMethod.GET, path = "getservices")
    public JsonNode getservices(@Named("Product") String id) {
        HashMap<String, JsonNode> product = dao.getProductsWithService();
        return product.get(id).get("services");
    }


    @ApiMethod(name = "delproduct", httpMethod = ApiMethod.HttpMethod.GET, path = "delproduct")
    public User delproduct(@Named("email") String email, @Named("index") Integer index) {
        User u=dao.get(email);
        u.delproduct(index);
        dao.save(u);
        return u;
    }

    @ApiMethod(name = "sendphoto", httpMethod = ApiMethod.HttpMethod.POST, path = "sendphoto")
    public User sendphoto(@Named("email") String email, JsonNode photo) {
        User u=dao.get(email);
        String url_photo=photo.get("photo").asText();
        Product p = dao.getProduct(u.getProducts().get(0));

        if(photo.get("type").asText().equals("product")){
            p.setPhoto(url_photo);
            dao.save(p);
        }
        if(photo.get("type").asText().equals("perso"))u.setPhoto(url_photo);
        dao.save(u);
        return u;
    }


    @ApiMethod(name = "getresp", httpMethod = ApiMethod.HttpMethod.GET, path = "getresp")
    public List<User> getresp(@Named("product_id") String product_id) {
        return dao.getUsersOfProduct(product_id);
    }


    @ApiMethod(name = "share", httpMethod = ApiMethod.HttpMethod.GET, path = "share")
    public HashMap<String, String> share(@Named("email") String email, @Named("dest") String dest, @Nullable @Named("firstname") String firstname) {
        User u=dao.get(email);
        User t=dao.get(dest);
        if(t!=null)return Tools.returnAPI(500,"Destinataire dejà enregistré",null);
        List<String> params = new ArrayList<>(Arrays.asList(
                "url_to_subscribe="+Tools.getDomainAppli()+"/login?email="+dest,
                "titre="+User.TITLE_APPLI,
                "origin.firstname="+u.getFirstname()));
        if(firstname!=null)params.add("firstname="+firstname);

        u.addPoints(10);
        dao.save(u);

        if(Tools.sendMail(dest,"Invitation de "+u.getFirstname()+" a rejoindre "+TITLE_APPLI,ADMIN_EMAIL,"invite",params))
            return Tools.returnAPI(200,"Mail envoyé",null);
        else
            return Tools.returnAPI(500);
    }


    @ApiMethod(name = "askforappointment", httpMethod = ApiMethod.HttpMethod.GET, path = "askforappointment")
    public HashMap<String, String> askforappointment(@Named("email") String email, @Named("durationInMin") Integer duration,@Named("dt") Long dt, @Nullable @Named("motif") String motif) {

        User u=dao.get(email);
        if(SuiteCRM.createFromUser(u,Leads.class))
            dao.save(u);

        Appointment a = new Appointment();
        a.setDtStart(dt);
        a.setUser(email);
        a.setDuration((long) (duration*1000*60));
        a.setConfirm(true);
        a.setMotif(motif);
        dao.save(a);
        return Tools.returnAPI(200,"Appointmenent added",null);
    }

    @ApiMethod(name = "getappointments", httpMethod = ApiMethod.HttpMethod.GET, path = "getappointments")
    public List<Appointment> getappointments(@Nullable @Named("email") String email) {
        List<Appointment> rc = dao.getAppointments(email);
        return rc;
    }

    @ApiMethod(name = "cancelappointments", httpMethod = ApiMethod.HttpMethod.GET, path = "cancelappointments")
    public List<Appointment> cancelappointments(@Named("email") String email,@Named("appointment") String id) {
        Appointment a = dao.getAppointment(id);
        if(a!=null)
            dao.delete(a);
        return dao.getAppointments(email);
    }

    @ApiMethod(name = "login", httpMethod = ApiMethod.HttpMethod.GET, path = "login")
    public User login(@Named("email") String email,@Nullable @Named("password") String password) {
        User u=dao.get(email);
        if(u!=null){
            if(password==null || password=="" || password.equals("null") || password.equals("undefined"))
                return dao.get(email);
            else{
                if(u.checkPassword(password)){
                    u.addConnexion();
                    dao.save(u);
                    return u;
                }
                else
                    return null;
            }
        }
        return u;
    }


    @ApiMethod(name = "addgift", httpMethod = ApiMethod.HttpMethod.GET, path = "addgift")
    public HashMap<String, String> addgift(@Named("email") String email, @Named("gift") String gift) {
        dao.addGift(email,gift);
        return Tools.returnAPI(200,"Gift added",null);
    }

    @ApiMethod(name = "resend_code", httpMethod = ApiMethod.HttpMethod.GET, path = "resend_code")
    public void resend_code(@Named("email") String email) {
        User u=dao.get(email);
        if(u!=null)
            u.sendPassword();
    }


    //http://localhost:8080/_ah/api/rousseau/v1/getprestations?modele=renault_clio
    //http://localhost:8080/_ah/api/rousseau/v1/getprestations?modele=BMW_serie3



    @ApiMethod(name = "getgifts", httpMethod = ApiMethod.HttpMethod.GET, path = "getgifts")
    public List<Gift> getgifts(@Nullable @Named("email") String email,@Nullable @Named("limit") Integer limit) {
        List<Gift> gifts = dao.getGifs(limit);
        //List<Gift> gifts = dao.getGifsFromCRM(limit);

        User u=dao.get(email);
        if(u==null)return gifts;

        List<Gift> rc=new ArrayList<>();
        for(Gift g:gifts){
            if(!u.contain(g) && g.getDtEnd()>System.currentTimeMillis() && g.getDtStart()<System.currentTimeMillis())
                rc.add(g);
        }
        return rc;

    }


//    test : http://localhost:8080/_ah/api/rousseau/v1/raz
    @ApiMethod(name = "raz", httpMethod = ApiMethod.HttpMethod.GET, path = "raz")
    public HashMap<String, String> raz() {
        dao.raz();

//        Gift g1=new Gift(
//                "50% sur vos nouveaux pneux",
//                "Rendez-vous directement dans le magasin, sans prendre rendez-vous avec votre véhicule",
//                "https://staticjn.1001pneus.fr/images/profils/ProfilsGoogle/ENERGY_SAVER.png",
//                "https://img.autoplus.fr/news/2018/07/26/1529508/1350%7C900%7C299cfbd66d72e472a85c7b9b.jpg?r",
//                0.1,0.0);
//
//        Gift g2=new Gift(
//                "une vidange offerte",
//                "Rendez-vous directement dans le magasin, sans prendre rendez-vous avec votre véhicule",
//                "http://motoconseil.fr/wp-content/uploads/2017/04/vidange7.png",
//                "https://nitifilter.com/wp-content/uploads/2015/08/20952739_l-1288x724.jpg"
//                ,0.2,0d);
//
//        Gift g3=new Gift(
//                "une révision de sécurité",
//                "Rendez-vous directement dans le magasin, sans prendre rendez-vous avec votre véhicule",
//                "https://www.gameandme.fr/wp-content/uploads/2007/04/search.png",
//                "https://img.autoplus.fr/news/2017/12/12/1522937/1350%7C900%7Cdc37824ada9ef507d4ddd68a.jpg?r"
//                ,0.05,0d);
//
//        Gift g4=new Gift(
//                "une journée en voiture de sport",
//                "Rendez-vous directement dans le magasin, sans prendre rendez-vous avec votre véhicule",
//                "http://download.seaicons.com/download/i41039/cemagraphics/classic-cars/cemagraphics-classic-cars-ferrari.ico",
//                "http://www.voitures-de-sport.net/wp-content/uploads/2015/03/voiture-sport.jpg"
//                ,0.5,2d);
//
//        g1.setCrmID(SuiteCRM.executeCRM(g1.toCampaign(), RequestType.Create));
//        g2.setCrmID(SuiteCRM.executeCRM(g2.toCampaign(), RequestType.Create));
//        g3.setCrmID(SuiteCRM.executeCRM(g3.toCampaign(), RequestType.Create));
//        g4.setCrmID(SuiteCRM.executeCRM(g4.toCampaign(), RequestType.Create));


        dao.loadProducts();
        dao.addGifts(Tools.loadDataFile("promotions").get("gifts"));

        return Tools.returnAPI(200,"Database erased",null);
    }



}
