package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.annotation.Subclass;

import java.io.Serializable;
import java.util.HashMap;

public abstract class product implements Serializable {
    protected String photo="";
    protected String label="";
    protected String id="";
    protected HashMap<String,Double> prestas=new HashMap<>();

    public product() {
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

    public HashMap<String, Double> getPrestas() {
        return prestas;
    }

    public void setPrestas(HashMap<String, Double> prestas) {
        this.prestas = prestas;
    }

    public abstract void initPrestas(String presta_file);
    public abstract void initPhoto(String modelesFile);

    public abstract boolean isValid();
}
