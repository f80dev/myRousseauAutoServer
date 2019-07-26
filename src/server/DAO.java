package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.*;
import com.sugaronrest.NameOf;
import com.sugaronrest.RequestType;
import com.sugaronrest.modules.Campaigns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class DAO {
    private static DAO dao = null;
    private static final Logger log = Logger.getLogger(DAO.class.getName());

    public static JsonNode server_settings;

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
        factory().register(Menu.class);
        factory().register(Item.class);
        factory().register(Product.class);
        factory().register(Enfant.class);
        factory().register(Reference.class);
        factory().register(Message.class);
        factory().register(Work.class);
        factory().register(Appointment.class);
        //SuiteCRM.init(User.CRM_USER,User.CRM_PASSWORD);

        try {
            server_settings=Tools.toJSON(Tools.rest(Tools.getDomainAppli()+"/assets/config.json"));
        } catch (RestAPIException e) {
            e.printStackTrace();
        }


    }

    public static void initItems(JsonNode jns) {
        for(JsonNode it:jns){
            save(new Item(it));
        }
    }

    public static void save(Item item) {
        ofy().save().entity(item);
    }


    public void raz(){
        ofy().delete().keys(ofy().load().type(User.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Message.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Item.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Work.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Reference.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Menu.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Product.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Gift.class).keys().list()).now();
        ofy().delete().keys(ofy().load().type(Appointment.class).keys().list()).now();
        //SuiteCRM.raz();
        dao.loadEnfants();
    }

    public Result<Key<Reference>> save(Reference r) {
        return ofy().save().entity(r);
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

    public Result<Key<User>> save(User u) {
        return ofy().save().entity(u);
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

    public void loadProducts(){
        JsonNode nodes = Tools.loadDataFile("products");
        for(JsonNode product:nodes.get("products")){
            Product p=new Product(product);
            p.setPhoto(nodes.get("default_photo").asText());
            dao.save(p);
        }
    }

    public void loadEnfants(){
        log.info("Chargement des enfants");
        JsonNode nodes = Tools.loadDataFile("products");
        if(nodes!=null){
            for(JsonNode product:nodes.get("products")){
                Product p=new Enfant(product);
                p.setPhoto(nodes.get("default_photo").asText());
                dao.save(p);
            }
        } else
            log.severe("Le fichier des produits ne semble pas disponibles");


    }

    public HashMap<String, JsonNode> getProductsWithService() {
        HashMap<String,JsonNode> products=new HashMap<>();
        HashMap<String,JsonNode> services=new HashMap<>();

        //TODO: ici on peut paramètre la localisation de la table de référence des produits
        JsonNode nodes = Tools.loadDataFile("products");

        int k=0;
//        for(JsonNode product:dao.getProducts()){
//            List<JsonNode> l_services=new ArrayList<>();
//            if(product.has("services")){
//                for(JsonNode serv:product.get("services")){
//                    String id=serv.get("id").asText();
//                    if(services.containsKey(id)){
//                        JsonNode _new = services.get(id);
//                        Iterator<String> iter = serv.fieldNames();
//                        while(iter.hasNext()){
//                            String fieldname=iter.next();
//                            ((ObjectNode)_new).put(fieldname,serv.get(fieldname).asText());
//                        }
//                        serv=_new;
//                    } else
//                        services.put(id,serv);
//
//                    l_services.add(serv);
//                }
//            }
//            ((ObjectNode) product).put("services",new ObjectMapper().valueToTree(l_services));
//            String id=""+(k++);
//            if(product.has("id"))id=product.get("id").asText();
//            ((ObjectNode) product).put("id",id);
//            if(!product.has("photo")){
//                String addr=nodes.get("default_photo").asText();
//                if(!addr.startsWith("http"))addr=Tools.getDomain()+addr;
//                ((ObjectNode) product).put("photo",addr);
//            }
//            products.put(id,product);
//        }
        return null;
    }

    /**
     *
     * @param email
     * @return la liste de tous les produits si l'email est vide
     */
    public List<Product> getProducts(String email) {
        if(email==null || email.length()==0)
            return ofy().load().type(Product.class).list();
        else {
            User u=get(email);
            Collection<Product> rc = ofy().load().type(Product.class).ids(u.getProducts()).values();
            return new ArrayList<>(rc);
        }
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
        ofy().save().entity(w).now();
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

        for(Product p:dao.getProducts(null))
            csv=csv.replaceAll("product_"+p.id,p.getLabel());

        return csv;
    }

    public void delete(Work w) {
        ofy().delete().entity(w).now();
    }

    public Work getWork(String workid) {
        return ofy().load().type(Work.class).id(workid).now();
    }

    public void save(Product p) {
        ofy().save().entity(p);
    }

    public Product getProduct(String id) {
        return ofy().load().type(Product.class).id(id).now();
    }

    public List<User> getUsersOfProduct(String product_id) {
        List<User> rc=new ArrayList<>();
        if(product_id.length()==0)return rc;
        for(User u:ofy().load().type(User.class).list()){
            if(u.getProducts().contains(product_id))rc.add(u);
        }
        return rc;
    }

    public List<Reference> getReferences(String category) {
        List<Reference> items = ofy().load().type(Reference.class).list();

        List<Reference> rc=new ArrayList<>();
        for(Reference it:items)
            if(category==null || category.length()==0 || it.getTags().indexOf(category)>-1)
                rc.add(it);
        return rc;
    }

    public Reference getReference(String refid) {
        return ofy().load().type(Reference.class).id(refid).now();
    }

    public List<Item> getItems(String category) {
        List<Item> rc=new ArrayList<>();
        for(Item it:ofy().load().type(Item.class).list())
            if(category==null || category.length()==0 || it.getTags().indexOf(category)>-1)
                rc.add(it);
        return rc;
    }

    public Menu findMenu(Long dtStart) {
        return ofy().load().type(Menu.class).filter("dtStart =",dtStart).first().now();
    }

    public Result<Key<Menu>> save(Menu m) {
        return ofy().save().entity(m);
    }

    public List<Menu> getMenusAfter(Long dtStart) {
        return ofy().load().type(Menu.class).filter("dtStart >",dtStart).order("dtStart").list();
    }

    public Long getNextDateForMenu(Long dtStart) {
        if(dtStart==null)dtStart=System.currentTimeMillis();
        List<Menu> l_m = ofy().load().type(Menu.class).order("dtStart").list();
        for(Menu m:l_m){
            if(m.getItems().size()<2 && m.dtStart>System.currentTimeMillis()){
                if(isOpen(m.dtStart))
                    return m.dtStart;
            }
        }

        Date _now=new Date(System.currentTimeMillis());
        if(_now.getHours()>12)_now=new Date(System.currentTimeMillis()+24*3600*1000);

        Long start=_now.getTime();
        if(l_m.size()>0){
            start=l_m.get(l_m.size()-1).dtStart+24*3600*1000;
        }

        return nextOpen(start);
    }

    public void deleteMenu(String idmenu) {
        ofy().delete().type(Menu.class).id(idmenu).now();
    }

    public static Boolean isOpen(Long dt){
        Date _dt=new Date();
        _dt.setTime(dt);
        if(_dt.getDay()==0 || _dt.getDay()==6)return false;

        for(JsonNode jn:server_settings.get("creche").get("dayoff")){
            String dt1=jn.asText().split(":")[0];
            String dt2=null;
            if(jn.asText().indexOf(":")>-1)dt2=jn.asText().split(":")[1];
            try {
                Date _dt1 = new SimpleDateFormat().parse(dt1+" "+server_settings.get("creche").get("open").asText());
                Date _dt2=null;
                if(dt2==null)
                    _dt2=new SimpleDateFormat().parse(dt1+" "+server_settings.get("creche").get("close").asText());
                else
                    _dt2=new SimpleDateFormat().parse(dt2+" "+server_settings.get("creche").get("close").asText());

                if(dt>_dt1.getTime() && dt<_dt2.getTime())return false;

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return true;
    }

    public Long nextOpen(Long dt){
        while(!isOpen(dt))
            dt=dt+24*3600*1000;
        return dt;
    }
}
