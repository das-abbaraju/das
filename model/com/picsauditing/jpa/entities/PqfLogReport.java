package com.picsauditing.jpa.entities;
// Generated Nov 15, 2007 2:20:12 PM by Hibernate Tools 3.2.0.b11


import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PqfLogReport generated by hbm2java
 */
@Entity
@Table(name="pqfdata"
)
public class PqfLogReport  implements java.io.Serializable {


     private PqfLogId id;
     private ContractorInfoReport contractorInfoReport;
     private Date dateVerified;

    public PqfLogReport() {
    }

    public PqfLogReport(PqfLogId id, ContractorInfoReport contractorInfoReport, Date dateVerified) {
       this.id = id;
       this.contractorInfoReport = contractorInfoReport;
       this.dateVerified = dateVerified;
    }
   
     @EmbeddedId
    
    @AttributeOverrides( {
        @AttributeOverride(name="conId", column=@Column(name="conID", nullable=false) ), 
        @AttributeOverride(name="questionId", column=@Column(name="questionID", nullable=false) ) } )
    public PqfLogId getId() {
        return this.id;
    }
    
    public void setId(PqfLogId id) {
        this.id = id;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="conID", nullable=false, insertable=false, updatable=false)
    public ContractorInfoReport getContractorInfoReport() {
        return this.contractorInfoReport;
    }
    
    public void setContractorInfoReport(ContractorInfoReport contractorInfoReport) {
        this.contractorInfoReport = contractorInfoReport;
    }
    @Temporal(TemporalType.DATE)
    @Column(name="dateVerified", nullable=false, length=10)
    public Date getDateVerified() {
        return this.dateVerified;
    }
    
    public void setDateVerified(Date dateVerified) {
        this.dateVerified = dateVerified;
    }




}


