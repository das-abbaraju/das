package com.picsauditing.jpa.entities;
// Generated Nov 15, 2007 2:20:12 PM by Hibernate Tools 3.2.0.b11


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Facility generated by hbm2java
 */
@Entity
@Table(name="facilities"
)
public class Facility  implements java.io.Serializable {


     private FacilityId id;

    public Facility() {
    }

    public Facility(FacilityId id) {
       this.id = id;
    }
   
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="opId", column=@Column(name="opID", nullable=false) ), 
        @AttributeOverride(name="corporateId", column=@Column(name="corporateID", nullable=false) ) } )
    public FacilityId getId() {
        return this.id;
    }
    
    public void setId(FacilityId id) {
        this.id = id;
    }




}


