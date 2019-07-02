package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

/**
 * Cette classe désigne un temps d'usage des produits
 *
 */

@Entity
public class Work implements Comparable<Work> {

    @Index
    private String owner="";

    @Index
    private Long dtStart=System.currentTimeMillis();

    @Index
    private Long dtEnd=System.currentTimeMillis()+110000;

    private String label="";

    @Index
    private String product_id="";


    @Id
    private String id="Work"+System.currentTimeMillis();

    public Work() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getDtStart() {
        return dtStart;
    }

    public void setDtStart(Long dtStart) {
        this.dtStart = dtStart;
    }

    public Long getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(Long dtEnd) {
        this.dtEnd = dtEnd;
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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    @OnSave
    void onSave(){

    }

    public String toCSV(String prefixe) {
        if(prefixe==null)prefixe="";
        int duration=Math.round((this.dtEnd-this.dtStart)/60000);
        return prefixe+this.product_id+";"+this.label+";"+this.dtEnd+";"+this.dtStart+";"+duration;
    }

    @Override
    public int compareTo(Work o) {
        if(o.dtStart>this.dtStart)return 1;
        return -1;
    }
}

