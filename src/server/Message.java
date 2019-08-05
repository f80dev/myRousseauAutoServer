package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import java.util.Date;

@Entity
public class Message {

    @Index
    private String author="";
    private String from="";

    @Index
    private Long dtStart=System.currentTimeMillis();

    @Index
    private Long dtEnd=System.currentTimeMillis()+110000;

    private String text="";
    private String photo="";

    @Id
    private String id="Message"+System.currentTimeMillis();

    public Message() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getDtStart() {
        return dtStart;
    }

    public void setDtStart(Long dtStart) {
        this.dtStart = dtStart;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(Long dtEnd) {
        this.dtEnd = dtEnd;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @OnSave
    void onSave(){

    }

}

