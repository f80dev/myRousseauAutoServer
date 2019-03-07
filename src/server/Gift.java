package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Random;

@Entity
public class Gift {

    @Id
    public String Id="gift"+System.currentTimeMillis()+new Random().longs();

    private String message="";
    private String messageColor="white";
    private String manual="";

    @Index
    private Long dtStart=0L;

    @Index
    private Long dtEnd=0L;

    private Long dtCreate=System.currentTimeMillis();
    private String picture="";
    private String icon="";


    @Index
    private Integer minFidelityPoints=0;


    public Gift() {
    }

    public Gift(String message, Long dtStart, Long dtEnd, String picture) {
        this.message = message;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.picture = picture;
        this.icon=this.picture;
    }

    public Gift(String message, String manual,String icon,String photo,Double durationInDays,Double delayInDays) {
        this.message = message;
        this.dtStart = Math.round(System.currentTimeMillis()+1000*3600*24*delayInDays);
        this.dtEnd = Math.round(System.currentTimeMillis()+1000*3600*24*durationInDays);
        this.picture = photo;
        this.icon=icon;
        this.manual=manual;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(String messageColor) {
        this.messageColor = messageColor;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getMinFidelityPoints() {
        return minFidelityPoints;
    }

    public void setMinFidelityPoints(Integer minFidelityPoints) {
        this.minFidelityPoints = minFidelityPoints;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Long getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Long dtCreate) {
        this.dtCreate = dtCreate;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
