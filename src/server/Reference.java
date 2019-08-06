package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cache
public class Reference implements Serializable  {
    @Id String ID="ref"+System.currentTimeMillis();

    Long dtCreate=System.currentTimeMillis();

    String url="";
    String text="";
    String address="";
    Double lat=0.0;
    Double lng=0.0;
    String comment="";
    String tags=""; //Peut être : boutique,livre,
    String owner=""; //reference à l'autheur

    List<String> likes=new ArrayList<>();
    List<String> dislikes=new ArrayList<>();

    public Reference() {
    }

    public Reference(JsonNode jn) {
        if(jn.has("address"))this.setAddress(jn.get("address").asText());
        if(jn.has("comment"))this.setComment(jn.get("comment").asText());
        if(jn.has("description"))this.setText(jn.get("description").asText());
        if(jn.has("title"))this.setText(jn.get("title").asText());
        if(jn.has("tags"))this.setTags(jn.get("tags").asText());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Long dtCreate) {
        this.dtCreate = dtCreate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setPosition(double lat, double lng) {
        this.setLat(lat);
        this.setLng(lng);
    }
}
