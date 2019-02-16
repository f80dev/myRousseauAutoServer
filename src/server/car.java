package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.HashMap;
import java.util.Map;


public class car {

    private String photo="";
    private String modele="";
    private HashMap<String,Double> prestas=new HashMap<>();

    public car() {
    }

    public car(String modele, String photo) {
        this.modele=modele;
        if(photo==null)
            this.initPhoto("modeles");
        else
            this.photo=photo;

        this.initPrestas("prestations");
    }


    public void initPrestas(String presta_file){
        this.prestas=new HashMap<>();
        for(JsonNode service:Tools.loadDataFile(presta_file))
            for(JsonNode tarif:service.get("tarifs")){
                String m=tarif.get("modele").asText();
                if(Tools.match(m,modele))
                    if(!this.prestas.containsKey(service.get("service").asText()))
                        this.prestas.put(service.get("service").asText(),tarif.get("prix").asDouble());
            }
    }


    public HashMap<String, Double> getPrestas() {
        return prestas;
    }

    public void setPrestas(HashMap<String, Double> prestas) {
        this.prestas = prestas;
    }

    public String getModele() {
        return modele;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public void initPhoto(String modelesFile) {
        for(JsonNode marque:Tools.loadDataFile(modelesFile))
            for(JsonNode modele:marque.get("modeles"))
                if(Tools.match(marque.get("marque").asText()+"/"+modele.get("reference").asText(),this.modele)){
                    this.photo=modele.get("photo").asText();
                    return;
                }


    }
}
