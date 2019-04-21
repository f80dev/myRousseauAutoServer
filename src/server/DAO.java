package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.sugaronrest.NameOf;
import com.sugaronrest.RequestType;
import com.sugaronrest.modules.Campaigns;

import java.util.*;
import java.util.logging.Logger;

public class DAO {
    private static DAO dao = null;
    private static final Logger log = Logger.getLogger(DAO.class.getName());

    public static Objectify ofy() {

        return ObjectifyService.ofy();
    }


    private DAO() {

    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

    static {
        factory().register(User.class);
        factory().register(Gift.class);
        factory().register(Message.class);
        factory().register(Work.class);
        factory().register(Appointment.class);
        SuiteCRM.init(User.CRM_USER,User.CRM_PASSWORD);
    }


    public void raz(){
        ofy().delete().keys(ofy().load().type(User.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Message.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Work.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Gift.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Appointment.class).keys().list()).now();
        SuiteCRM.raz();
    }




    public static synchronized DAO getInstance() {
        if (null == dao) {
            dao = new DAO();
        }
        return dao;
    }


    public User get(String user) {
        if(user==null || user.length()==0)return null;
        return ofy().load().type(User.class).id(user).now();
    }

    public void save(User u) {
        ofy().save().entity(u);
    }

    public void save(Appointment a) {
        ofy().save().entity(a);
    }

    public List<Appointment> getAppointments(String email) {
        if(email==null)
            return ofy().load().type(Appointment.class).filter("confirm",false).list();
        else
            return ofy().load().type(Appointment.class).filter("user",email).filter("confirm",true).list();
    }

    public void addGift(String email, String gift) {
        List<User> l_users = getUsers(email);
        for(User u:l_users){
            u.addGift(gift);
            save(u);
        }
    }

    public List<User> getUsers(String email) {
        List<User> l_users=null;
        if(email==null)
            l_users=ofy().load().type(User.class).list();
        else
            l_users = Arrays.asList(get(email));
        return l_users;
    }

    public void save(Gift g) {
        ofy().save().entity(g).now();
    }

    public List<Gift> getGifs(Integer limit) {
        return ofy().load().type(Gift.class).limit(limit).list();
    }

    public List<Gift> getGifsFromCRM(Integer limit) {
        if(limit==null)limit=1000;
        List<Campaigns> result=SuiteCRM.readCRM("Campaigns",
                limit,
                Arrays.asList(
                        NameOf.Campaigns.Id,
                        NameOf.Campaigns.Name,
                        NameOf.Campaigns.Content,
                        NameOf.Campaigns.TrackerKey,
                        NameOf.Campaigns.StartDate,
                        NameOf.Campaigns.EndDate),
                RequestType.BulkRead);

        List<Gift> rc=new ArrayList<>();
        for(Campaigns c:result)
            rc.add(new Gift(c));

        return rc;
    }

    public Appointment getAppointment(String id) {
        return ofy().load().type(Appointment.class).id(id).now();
    }

    public void delete(Appointment a) {
        ofy().delete().entity(a).now();
    }

    public HashMap<String, JsonNode> getProducts() {
        HashMap<String,JsonNode> products=new HashMap<>();
        HashMap<String,JsonNode> services=new HashMap<>();
        JsonNode nodes = Tools.loadDataFile("products");
        int k=0;
        for(JsonNode product:nodes.get("products")){
            List<JsonNode> l_services=new ArrayList<>();
            if(product.has("services")){
                for(JsonNode serv:product.get("services")){
                    String id=serv.get("id").asText();
                    if(services.containsKey(id)){
                        JsonNode _new = services.get(id);
                        Iterator<String> iter = serv.fieldNames();
                        while(iter.hasNext()){
                            String fieldname=iter.next();
                            ((ObjectNode)_new).put(fieldname,serv.get(fieldname).asText());
                        }
                        serv=_new;
                    } else
                        services.put(id,serv);

                    l_services.add(serv);
                }
            }
            ((ObjectNode) product).put("services",new ObjectMapper().valueToTree(l_services));
            String id=""+(k++);
            if(product.has("id"))id=product.get("id").asText();
            ((ObjectNode) product).put("id",id);
            if(!product.has("photo")){
                String addr=nodes.get("default_photo").asText();
                if(!addr.startsWith("http"))addr=Tools.getDomain()+addr;
                ((ObjectNode) product).put("photo",addr);
            }
            products.put(id,product);
        }
        return products;
    }

    public void addGifts(JsonNode jsonNode) {
        for(JsonNode jn:jsonNode){
            dao.save(new Gift(jn));
        }
    }

    public List<Message> getMessages(User user) {
        List<Message> messages = ofy().load().type(Message.class).filter("dtStart <", System.currentTimeMillis()).list();
        List<Message> rc=new ArrayList<>();
        for(Message m:messages){
            if(!user.alreadyView(m.getId()) && m.getDtEnd()>System.currentTimeMillis())
                rc.add(m);
        }
        return rc;
    }

    public void save(Message m) {
        ofy().save().entity(m);
    }

    public void save(Work w) {
        ofy().save().entity(w);
    }

    public List<Work> getWorks(String productid) {
        if(productid==null)
            return ofy().load().type(Work.class).list();
        else
            return ofy().load().type(Work.class).filter("product_id",productid).list();
    }

    public String exportWorksToCSV() {
        String csv="id;label;dtStart;dtEnd;duration\n";
        for(Work w:dao.getWorks(null))
            csv+=w.toCSV("product_")+"\n";

        HashMap<String, JsonNode> products = this.getProducts();
        for(String k:products.keySet())
            csv=csv.replaceAll("product_"+k,products.get(k).get("label").asText());

        return csv;
    }
}
