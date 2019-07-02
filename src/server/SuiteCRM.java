package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.sugaronrest.*;
import com.sugaronrest.modules.*;

import java.util.*;
import java.util.logging.Logger;

public class SuiteCRM extends CRM {
    private static final Logger log = Logger.getLogger("SuiteCRM");
    private static SugarRestClient crm=null;

    public SuiteCRM(){}

    public static Boolean init(String user, String password) {
        crm=new SugarRestClient(Tools.getCRMServer()+"/service/v4_1/rest.php",user,password);
        return crm!=null;
    }
 
    public static Boolean createFromUser(User u,Class c) {
        SugarRestRequest _r = new SugarRestRequest(c, RequestType.Create);
        _r .setParameter(u.toContact());
        return updateUser(u,crm.execute(_r));
    }


    public static boolean updateUser(User u,SugarRestResponse resp){
        if(resp.getStatusCode()==200){
            String id=resp.getJData().substring(1,resp.getJData().length()-1);
            u.setCrm_contactsID(id);
            return true;
        } else {
            log.severe("pas d'ajout dans le CRM "+resp.getError().getMessage());
        }
        return false;
    }

    public static <T> String executeCRM(T obj,RequestType rt) {
        SugarRestRequest r_objs = new SugarRestRequest(obj.getClass().getSimpleName(), rt);
        r_objs.setParameter(obj);
        SugarRestResponse resp=crm.execute(r_objs);
        if(resp.getStatusCode()==200){
            return resp.getJData().substring(1,resp.getJData().length()-1);
        } else {
            log.severe("pas d'ajout dans le CRM "+resp.getError().getMessage());
        }
        return null;
    }

    public static void deleteCRM(String moduleName,String id) {
        SugarRestRequest r = new SugarRestRequest(moduleName, RequestType.Delete);
        r.setParameter(id);
        SugarRestResponse resp=crm.execute(r);
    }

    //https://support.sugarcrm.com/Documentation/Sugar_Developer/Sugar_Developer_Guide_8.2/Cookbook/Web_Services/REST_API/Bash/How_to_Export_a_List_of_Records/
    public static Boolean updateCRM(User u)  {
        SugarRestRequest r_contacts = new SugarRestRequest(Contacts.class, RequestType.Update);
        r_contacts.setParameter(u.toContact());
        SugarRestResponse resp = crm.execute(r_contacts);
        return resp.getStatusCode()==200;
    }

    public static <T> List<T> readCRM(String moduleName,int maxRespons, List<String> selectFields, RequestType requestType) {
        SugarRestRequest request = new SugarRestRequest(moduleName, requestType);
        request.getOptions().setSelectFields(selectFields);
        request.getOptions().setMaxResult(maxRespons);
        SugarRestResponse response = crm.execute(request);
        if(response.getStatusCode()!=200){
            log.severe(response.getError().getMessage());
            return null;
        }
        return (List<T>) response.getData();
    }


    public static void raz() {
        List<Campaigns> l_camp=readCRM("Campaigns",100, Arrays.asList(NameOf.Campaigns.Id),RequestType.BulkRead);
        if(l_camp!=null)
            for(Campaigns c:l_camp)deleteCRM("Campaigns",c.getId());

        List<Contacts> lc=readCRM("Contacts",100, Arrays.asList(NameOf.Contacts.Id),RequestType.BulkRead);
        if(lc!=null)
            for(Contacts c:lc)deleteCRM("Contacts",c.getId());

        List<Leads> ll=readCRM("Leads",100, Arrays.asList(NameOf.Leads.Id),RequestType.BulkRead);
        if(ll!=null)
            for(Leads c:ll)deleteCRM("Leads",c.getId());

        List<Prospects> lp=readCRM("Prospects",100, Arrays.asList(NameOf.Prospects.Id),RequestType.BulkRead);
        if(lp!=null)
            for(Prospects c:lp)deleteCRM("Prospects",c.getId());
    }

    public static JsonNode read(String modulePere, String moduleFils, List<String> fieldsPere, List<String> fieldsFils){
        SugarRestRequest request = new SugarRestRequest(modulePere, RequestType.LinkedBulkRead);
        request.getOptions().setSelectFields(fieldsPere);

        Map<Object, List<String>> linkedListInfo = new HashMap<Object, List<String>>();
        linkedListInfo.put(moduleFils, fieldsFils);
        request.getOptions().setLinkedModules(linkedListInfo);

        SugarRestResponse response = crm.execute(request);
        return Tools.toJSON(response.getJData());
    }

    public static void readRelation2(){

    }



}
