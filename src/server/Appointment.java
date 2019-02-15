package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Appointment {

    @Index
    private String user="";
    private Long dtStart=0L;

    @Index
    private Boolean confirm=false;

    private String motif="";

    @Id
    private String id="Appointment"+System.currentTimeMillis();

    public Appointment() {
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getDtStart() {
        return dtStart;
    }

    public void setDtStart(Long dtStart) {
        this.dtStart = dtStart;
    }

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }
}
