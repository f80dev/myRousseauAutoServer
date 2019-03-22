package server;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.sugaronrest.NameOf;
import com.sugaronrest.RequestType;
import com.sugaronrest.modules.Campaigns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        factory().register(Appointment.class);
        SuiteCRM.init(User.CRM_USER,User.CRM_PASSWORD);
    }


    public void raz(){
        ofy().delete().keys(ofy().load().type(User.class).keys().list()).now();
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
}
