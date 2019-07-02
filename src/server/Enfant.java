package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.annotation.Subclass;

import java.util.HashMap;

@Subclass(index=true)
public class Enfant extends Product {

    private Integer forfait=0;

    public Enfant() {
        super();
    }

    public Enfant(JsonNode product) {
        super(product);
        if(product.has("forfait"))this.forfait=product.get("forfait").asInt();
    }

    public Integer getForfait() {
        return forfait;
    }

    public void setForfait(Integer forfait) {
        this.forfait = forfait;
    }

}
