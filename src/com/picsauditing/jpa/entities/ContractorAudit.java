package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.access.Permissions;

@Entity
@Table(name = "contractor_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAudit implements java.io.Serializable {
	private int id = 0;
	private AuditType auditType;
	private ContractorAccount contractorAccount;
	private Date createdDate = new Date();
	private AuditStatus auditStatus = AuditStatus.Pending;
	private Date expiresDate;
	private User auditor;
	private Date assignedDate;
	private Date scheduledDate;
	private Date completedDate;
	private Date closedDate;
	private OperatorAccount requestingOpAccount;
	private String auditLocation;
	private int percentComplete;
	private int percentVerified;
	private Date contractorConfirm;
	private Date auditorConfirm;
	private boolean manuallyAdded;
	private String auditFor;
	
	private List<AuditCatData> categories = new ArrayList<AuditCatData>();
	private List<AuditData> data = new ArrayList<AuditData>();
	private List<OshaAudit> oshas = new ArrayList<OshaAudit>();
	private List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auditID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@OneToMany(mappedBy = "conAudit", cascade = { CascadeType.REMOVE })
	@OrderBy("location")
	public List<OshaAudit> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaAudit> oshas) {
		this.oshas = oshas;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.REMOVE })
	public List<ContractorAuditOperator> getOperators() {
		return operators;
	}

	public void setOperators(List<ContractorAuditOperator> operators) {
		this.operators = operators;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.AuditStatus") })
	@Enumerated(EnumType.STRING)
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		if (auditStatus != null && this.auditStatus != null && !auditStatus.equals(this.auditStatus)) {
			// If we're changing the status to Submitted or Active, then we need
			// to set the dates
			if (auditStatus.equals(AuditStatus.Pending)) {
				if (closedDate != null)
					closedDate = null;
				if(completedDate != null)
					completedDate = null;
				if(expiresDate != null)
					expiresDate = null;
			}
			if (auditStatus.equals(AuditStatus.Submitted) || auditStatus.equals(AuditStatus.Resubmitted)) {
				// If we're going "forward" then (re)set the closedDate
				if (completedDate == null)
					completedDate = new Date();
			}
			if (auditStatus.equals(AuditStatus.Active)) {
				// If we're going "forward" then (re)set the closedDate
				if (closedDate == null || this.auditStatus.isPendingSubmitted())
					closedDate = new Date();

				// If we're closed, there should always be a completedDate,
				// so fill it in if it hasn't already been set
				if (completedDate == null)
					completedDate = closedDate;
			}
		}
		this.auditStatus = auditStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	@ManyToOne
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByOpID")
	public OperatorAccount getRequestingOpAccount() {
		return requestingOpAccount;
	}

	public void setRequestingOpAccount(OperatorAccount requestingOpAccount) {
		this.requestingOpAccount = requestingOpAccount;
	}

	public String getAuditLocation() {
		return auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}

	public int getPercentVerified() {
		return percentVerified;
	}

	public void setPercentVerified(int percentVerified) {
		this.percentVerified = percentVerified;
	}

	// Child tables

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

	// TRANSIENT ///////////////////////////////

	@Transient
	public int getPercent() {
		if (AuditStatus.Pending.equals(auditStatus))
			return this.percentComplete;
		if (AuditStatus.Submitted.equals(auditStatus))
			return this.percentVerified;

		return 100;
	}

	@Transient
	// I think we should move this to AuditActionSupport instead (Trevor 5/7/08)
	public boolean isCanView(Permissions permissions) {
		if (permissions.isContractor() && (getAuditType().isCanContractorView() == false))
			return false;
		else
			return true;
	}

	/**
	 * 
	 * 
	 * @return True if audit expires this year and it's before March 1
	 */
	@Transient
	public boolean isAboutToExpire() {
		Calendar current = Calendar.getInstance();
		Calendar expires = Calendar.getInstance();
		expires.setTime(expiresDate);
		
		if (current.get(Calendar.MONTH) < 2 
				&& current.get(Calendar.YEAR) == expires.get(Calendar.YEAR))
			return true;
		
		return false;
	}

	@Transient
	public Date getEffectiveDate() {
		if (auditStatus.equals(AuditStatus.Pending)) {
			if (auditor != null && assignedDate != null)
				return assignedDate;
			return createdDate;
		}
		if (auditStatus.equals(AuditStatus.Submitted))
			return completedDate;
		return closedDate;
	}
	
	@Transient
	public Date getValidDate() {
		if (auditType.isPqf())
			return new Date();
		else
			return createdDate;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ContractorAudit other = (ContractorAudit) obj;
		if (id != other.getId())
			return false;
		return true;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getContractorConfirm() {
		return contractorConfirm;
	}

	public void setContractorConfirm(Date contractorConfirm) {
		this.contractorConfirm = contractorConfirm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAuditorConfirm() {
		return auditorConfirm;
	}

	public void setAuditorConfirm(Date auditorConfirm) {
		this.auditorConfirm = auditorConfirm;
	}

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	/**
	 * Who, what, or when is this audit for? Examples: 
	 * OSHA/EMR for "2005" IM for "John Doe" 
	 * @return
	 */
	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

}
