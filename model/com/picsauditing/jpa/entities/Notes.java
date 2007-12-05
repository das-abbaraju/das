package com.picsauditing.jpa.entities;
// Generated Nov 15, 2007 2:20:12 PM by Hibernate Tools 3.2.0.b11


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Notes generated by hbm2java
 */
@Entity
@Table(name="notes"
)
public class Notes  implements java.io.Serializable {


     private Integer noteId;
     private Date timeStamp;
     private int opId;
     private int conId;
     private String whoIs;
     private String note;
     private boolean isDeleted;
     private Date deletedDate;
     private String whoDeleted;

    public Notes() {
    }

    public Notes(int opId, int conId, String whoIs, String note, boolean isDeleted, Date deletedDate, String whoDeleted) {
       this.opId = opId;
       this.conId = conId;
       this.whoIs = whoIs;
       this.note = note;
       this.isDeleted = isDeleted;
       this.deletedDate = deletedDate;
       this.whoDeleted = whoDeleted;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="noteID", nullable=false)
    public Integer getNoteId() {
        return this.noteId;
    }
    
    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }
    @Version
    @Column(name="timeStamp", nullable=false)
    public Date getTimeStamp() {
        return this.timeStamp;
    }
    
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    @Column(name="opID", nullable=false)
    public int getOpId() {
        return this.opId;
    }
    
    public void setOpId(int opId) {
        this.opId = opId;
    }
    
    @Column(name="conID", nullable=false)
    public int getConId() {
        return this.conId;
    }
    
    public void setConId(int conId) {
        this.conId = conId;
    }
    
    @Column(name="whoIs", nullable=false, length=100)
    public String getWhoIs() {
        return this.whoIs;
    }
    
    public void setWhoIs(String whoIs) {
        this.whoIs = whoIs;
    }
    
    @Column(name="note", nullable=false, length=16277215)
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    @Column(name="isDeleted", nullable=false)
    public boolean isIsDeleted() {
        return this.isDeleted;
    }
    
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @Column(name="deletedDate", nullable=false, length=19)
    public Date getDeletedDate() {
        return this.deletedDate;
    }
    
    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }
    
    @Column(name="whoDeleted", nullable=false, length=100)
    public String getWhoDeleted() {
        return this.whoDeleted;
    }
    
    public void setWhoDeleted(String whoDeleted) {
        this.whoDeleted = whoDeleted;
    }




}


