package com.picsauditing.jpa.entities;

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

@Entity
@Table(name = "pqfdata")
public class PqfLogReport implements java.io.Serializable {

	private PqfDataKey id;
	private ContractorInfoReport contractorInfoReport;
	private Date dateVerified;

	public PqfLogReport() {
	}

	public PqfLogReport(PqfDataKey id, ContractorInfoReport contractorInfoReport, Date dateVerified) {
		this.id = id;
		this.contractorInfoReport = contractorInfoReport;
		this.dateVerified = dateVerified;
	}

	@EmbeddedId
	@AttributeOverrides( { @AttributeOverride(name = "conId", column = @Column(name = "conID", nullable = false)),
			@AttributeOverride(name = "questionId", column = @Column(name = "questionID", nullable = false)) })
	public PqfDataKey getId() {
		return this.id;
	}

	public void setId(PqfDataKey id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID", nullable = false, insertable = false, updatable = false)
	public ContractorInfoReport getContractorInfoReport() {
		return this.contractorInfoReport;
	}

	public void setContractorInfoReport(ContractorInfoReport contractorInfoReport) {
		this.contractorInfoReport = contractorInfoReport;
	}

	// @Temporal(TemporalType.DATE)
	@Column(name = "dateVerified", nullable = false, length = 10)
	public Date getDateVerified() {
		return this.dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

}
