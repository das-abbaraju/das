package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "generalcontractors")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="temp")
public class ContractorOperator implements java.io.Serializable {
	private static final long serialVersionUID = 7554304496743322510L;
	
	private int id;
	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private Date dateAdded;
	private String workStatus = "P";
	private FlagColor forceFlag;
	private Date forceBegin;
	private Date forceEnd;
	private ContractorOperatorFlag flag;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "genID", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "subID", nullable = false, updatable = false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateAdded() {
		return this.dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}

	@Temporal(TemporalType.DATE)
	public Date getForceBegin() {
		return forceBegin;
	}

	public void setForceBegin(Date forceBegin) {
		this.forceBegin = forceBegin;
	}

	@Temporal(TemporalType.DATE)
	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}

	@Transient
	public boolean isForcedFlag() {
		Date now = new Date();

		if (forceEnd != null) {
			if (now.after(forceEnd)) {
				// This force has expired, so remove it
				forceBegin = null;
				forceEnd = null;
				forceFlag = null;
				return false;
			}
		}

		if (forceBegin == null || now.compareTo(forceBegin) >= 0) {
			// This flag is in effect as long as it's not null
			return forceFlag != null;
		}
		// The flag is not yet in effect
		return false;
	}

	@OneToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumns({
		@JoinColumn(name="genID", referencedColumnName="opID", insertable=false, updatable=false),
		@JoinColumn(name="subID", referencedColumnName="conID", insertable=false, updatable=false)
	})
	public ContractorOperatorFlag getFlag() {
		return flag;
	}

	public void setFlag(ContractorOperatorFlag flag) {
		this.flag = flag;
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
		final ContractorOperator other = (ContractorOperator) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
}
