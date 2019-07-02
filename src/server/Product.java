package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.HashMap;

@Cache
@Entity
public class Product {
    protected String photo="";
    protected String label="";

    @Id
    protected String id="product"+System.currentTimeMillis();

    private Long dtStartWork=0L; //Utilisé pour créer des work a la volé

    protected HashMap<String,Double> prestas=new HashMap<>();

    public Product() {
    }

    public Product(JsonNode product) {
        if(product.has("photo"))this.photo=product.get("photo").asText();
        if(product.has("label"))this.label=product.get("label").asText();
        if(product.has("id"))this.id=product.get("id").asText();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDtStartWork() {
        return dtStartWork;
    }

    public void setDtStartWork(Long dtStartWork) {
        if(dtStartWork==null)dtStartWork=System.currentTimeMillis();
        this.dtStartWork = dtStartWork;
    }

    public HashMap<String, Double> getPrestas() {
        return prestas;
    }

    public void setPrestas(HashMap<String, Double> prestas) {
        this.prestas = prestas;
    }

//    public abstract void initPrestas(String presta_file);
//    public abstract void initPhoto(String modelesFile);
    public boolean isValid(){
        return true;
    }
}
