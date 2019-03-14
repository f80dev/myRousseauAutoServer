package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.sugaronrest.RequestType;
import com.sugaronrest.modules.Meetings;

import java.util.Date;

@Entity
public class Appointment {

    @Index
    private String user="";
    private Long dtStart=0L;
    private Long duration=0L;

    @Index
    private Boolean confirm=false;

    @Index
    private String crmID="";

    private String motif="";

    @Id
    private String id="Appointment"+System.currentTimeMillis();

    public Appointment() {
    }

    public String getCrmID() {
        return crmID;
    }

    public void setCrmID(String crmID) {
        this.crmID = crmID;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public Meetings toMeetings(){
        Meetings m=new Meetings();
        m.setDateStart(new Date(this.getDtStart()));
        m.setDateEntered(new Date(this.getDtStart()));
        m.setDurationHours(Math.toIntExact(this.getDuration() / (3600 * 1000)));
        m.setDurationMinutes(Math.toIntExact(this.getDuration() / (60 * 1000)) % 60);
        m.setName(this.getMotif());
        m.setDateEnd(new Date(this.getDtStart()+this.getDuration()));
        return m;
    }

    @OnSave
    void onSave(){
        this.setCrmID(Tools.executeCRM(this.toMeetings(), RequestType.Create));
    }

}

