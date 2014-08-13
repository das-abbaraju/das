package com.picsauditing.auditbuilder.entities;

import com.picsauditing.auditbuilder.util.Grepper;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author PICS
 * 
 */
@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorAudit")
@Table(name = "contractor_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorAudit extends BaseTable {

	private AuditType auditType;
	private ContractorAccount contractorAccount;
	private Date expiresDate;
	private Date effectiveDate;
	private OperatorAccount requestingOpAccount;
	private int score;
	private boolean manuallyAdded;
	private String auditFor;
	private Date lastRecalculation;
	private Date scheduledDate;

	private List<AuditCatData> categories = new ArrayList<>();
	private List<AuditData> data = new ArrayList<>();
	private List<ContractorAuditOperator> operators = new ArrayList<>();
	private ContractorAudit previousAudit;

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractor) {
		this.contractorAccount = contractor;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
	public List<ContractorAuditOperator> getOperators() {
		return operators;
	}

	public void setOperators(List<ContractorAuditOperator> operators) {
		this.operators = operators;
	}

	@Transient
	public List<ContractorAuditOperator> getOperatorsVisible() {
		return new Grepper<ContractorAuditOperator>() {

			public boolean check(ContractorAuditOperator t) {
				return t.isVisible();
			}
		}.grep(this.operators);
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@ManyToOne
	@JoinColumn(name = "previousAuditID")
	public ContractorAudit getPreviousAudit() {
		return previousAudit;
	}

	public void setPreviousAudit(ContractorAudit previousAudit) {
		this.previousAudit = previousAudit;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByOpID")
	public OperatorAccount getRequestingOpAccount() {
		return requestingOpAccount;
	}

	public void setRequestingOpAccount(OperatorAccount requestingOpAccount) {
		this.requestingOpAccount = requestingOpAccount;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
	public List<AuditCatData> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCatData> categories) {
		this.categories = categories;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
	public List<AuditData> getData() {
		return data;
	}

	public void setData(List<AuditData> data) {
		this.data = data;
	}

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public Date getLastRecalculation() {
		return lastRecalculation;
	}

	public void setLastRecalculation(Date lastRecalculation) {
		this.lastRecalculation = lastRecalculation;
	}
}