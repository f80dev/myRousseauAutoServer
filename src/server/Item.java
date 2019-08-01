package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class Item {
    @Id
    String title="";

    String description="";
    String from=null;
    String tags=""; //Peut être : boutique,livre,
    String recette="";


    public Item() {
    }

    public Item(JsonNode jn) {
        if(jn.has("title"))this.setTitle(jn.get("title").asText());
        if(jn.has("titre"))this.setTitle(jn.get("titre").asText());
        if(jn.has("description"))this.setDescription(jn.get("description").asText());
        if(jn.has("from"))this.setFrom(jn.get("from").asText());
        if(jn.has("recette"))this.setRecette(jn.get("recette").asText());
        if(jn.has("tags"))this.setTags(jn.get("tags").asText());
        if(jn.has("category")){
            for(JsonNode cat:jn.get("category")){
                this.setTags(this.getTags()+","+cat.asText());
            }
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        while(tags.endsWith(","))
            tags=tags.substring(0,tags.length()-1);
        this.tags = tags;
    }

    public String getRecette() {
        return recette;
    }

    public void setRecette(String recette) {
        this.recette = recette;
    }

}
