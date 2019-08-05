package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Menu {

    @Id
    String id="menu"+System.currentTimeMillis();

    @Index
    Long dtStart=null;

    User preparateur=null;
    List<Item> items=new ArrayList<>();
    String groupe="";

    public Menu() {
    }

    public Menu(Long dtStart,User preparateur,String groupe) {
        this.preparateur=preparateur;
        this.groupe=groupe;
        this.dtStart=dtStart;
    }


    public String getGroupe() {
        return groupe;
    }

    public void setGroupe(String groupe) {
        this.groupe = groupe;
    }

    public Long getDtStart() {
        return dtStart;
    }

    public void setDtStart(Long dtStart) {
        this.dtStart = dtStart;
    }

    public User getPreparateur() {
        return preparateur;
    }

    public void setPreparateur(User preparateur) {
        this.preparateur = preparateur;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void add(Item it) {
        if(!this.items.contains(it))this.items.add(it);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isGroupe(String groupe) {
        if(groupe==null || this.groupe==null || this.groupe.equals(groupe))return true;
        return false;
    }
}
