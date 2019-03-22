package server;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;

public class room extends product  {
    public room() {
        super();
    }

    public room(String id) {
        super();
        this.id=id;
    }

    @Override
    public void initPrestas(String presta_file){
        this.prestas=new HashMap<>();
        for(JsonNode service:Tools.loadDataFile(presta_file))
            for(JsonNode tarif:service.get("tarifs")){
                String m=tarif.get("modele").asText();
                if(Tools.match(m,id))
                    if(!this.prestas.containsKey(service.get("service").asText()))
                        this.prestas.put(service.get("service").asText(),tarif.get("prix").asDouble());
            }
    }

    public void initPhoto(String productFiles) {

    }

    @Override
    public boolean isValid() {
        return true;
    }

}
