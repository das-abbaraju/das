package com.picsauditing.jpa.entities;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BasicDynaBean;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "generalcontractors")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorOperator extends BaseTable implements java.io.Serializable {

	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private String workStatus = "P";
	private FlagColor flagColor;
	private FlagColor baselineFlag;
	private FlagColor forceFlag;
	private Date flagLastUpdated;
	private Date forceEnd;
	private Date forceBegin;
	private User forcedBy;
	private WaitingOn waitingOn = WaitingOn.None;
	private Date processCompletion;
	private String relationshipType;
	private Set<FlagData> flagDatas = new HashSet<FlagData>();
	private Set<FlagDataOverride> overrides = new HashSet<FlagDataOverride>();

	@ManyToOne
	@JoinColumn(name = "genID", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operator) {
		this.operatorAccount = operator;
	}

	@ManyToOne
	@JoinColumn(name = "subID", nullable = false, updatable = false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractor) {
		this.contractorAccount = contractor;
	}

	/**
	 * Assume Yes if the operator approvesRelationships=No, otherwise this
	 * should default to P and then be approved or rejected
	 * 
	 * @return P=Pending, Y=Yes, N=No
	 */
	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	@Transient
	public boolean isWorkStatusApproved() {
		if (!getOperatorAccount().isCorporate())
			return "Y".equals(workStatus);
		else
			return isChildrenWorkStatusEqual("Y");
	}

	@Transient
	public boolean isWorkStatusRejected() {
		if (!getOperatorAccount().isCorporate())
			return "N".equals(workStatus);
		else
			return isChildrenWorkStatusEqual("N");
	}

	@Transient
	public boolean isWorkStatusPending() {
		if (!getOperatorAccount().isCorporate())
			return "P".equals(workStatus);
		else
			return isChildrenWorkStatusEqual("P");
	}

	private boolean isChildrenWorkStatusEqual(String parentStatus) {
		String where = "subid = " + getContractorAccount().getId() + " AND workStatus = '" + parentStatus + "'";
		Set<Integer> idList = new HashSet<Integer>();
		for (OperatorAccount o : getOperatorAccount().getOperatorChildren())
			idList.add(o.getId());
		String ids = Strings.implode(idList, ",");
		where += " AND genid IN (" + ids + ")";

		SelectSQL sql = new SelectSQL("generalcontractors", where);
		sql.setLimit(1);

		try {
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), false);
			return pageData.size() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getBaselineFlag() {
		return baselineFlag;
	}

	public void setBaselineFlag(FlagColor baselineFlag) {
		this.baselineFlag = baselineFlag;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}

	@Temporal(TemporalType.DATE)
	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}

	public Date getForceBegin() {
		return forceBegin;
	}

	public void setForceBegin(Date forceBegin) {
		this.forceBegin = forceBegin;
	}

	@ManyToOne
	@JoinColumn(name = "forcedBy")
	public User getForcedBy() {
		return forcedBy;
	}

	public void setForcedBy(User forcedBy) {
		this.forcedBy = forcedBy;
	}

	@Temporal(TemporalType.DATE)
	public Date getProcessCompletion() {
		return processCompletion;
	}

	public void setProcessCompletion(Date processCompletion) {
		this.processCompletion = processCompletion;
	}

	@Transient
	public boolean isForcedFlag() {
		if (forceFlag == null || forceEnd == null) {
			// Just double check they are both set back to null
			removeForceFlag();
			return false;
		}

		// We have a forced flag, but make sure it's still in effect
		if (forceEnd.before(new Date())) {
			removeForceFlag();
			return false;
		}
		return true;
	}

	@Transient
	public void removeForceFlag() {
		forceEnd = null;
		forceFlag = null;
		forceBegin = null;
		forcedBy = null;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "flag", nullable = false)
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	public Date getFlagLastUpdated() {
		return flagLastUpdated;
	}

	public void setFlagLastUpdated(Date flagLastUpdated) {
		this.flagLastUpdated = flagLastUpdated;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "waitingOn", nullable = false)
	public WaitingOn getWaitingOn() {
		return waitingOn;
	}

	public void setWaitingOn(WaitingOn waitingOn) {
		this.waitingOn = waitingOn;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	// added mappedBy="flag" to show which side is the owning side in the
	// relationship
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "flag")
	@JoinColumns( { @JoinColumn(name = "opID", referencedColumnName = "genID"),
			@JoinColumn(name = "conID", referencedColumnName = "subID") })
	public Set<FlagData> getFlagDatas() {
		return flagDatas;
	}

	public void setFlagDatas(Set<FlagData> flagDatas) {
		this.flagDatas = flagDatas;
	}

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, mappedBy = "forceflag")
	@JoinColumns( { @JoinColumn(name = "opID", referencedColumnName = "genID"),
			@JoinColumn(name = "conID", referencedColumnName = "subID") })
	public Set<FlagDataOverride> getOverrides() {
		return overrides;
	}

	public void setOverrides(Set<FlagDataOverride> overrides) {
		this.overrides = overrides;
	}

	@Transient
	public ContractorOperator getForceOverallFlag() {
		if (isForcedFlag())
			return this;
		if (getOperatorAccount().getCorporateFacilities().size() > 0) {
			for (Facility facility : getOperatorAccount().getCorporateFacilities()) {
				for (ContractorOperator conOper : contractorAccount.getOperators()) {
					if (facility.getCorporate().equals(conOper.getOperatorAccount()) && conOper.isForcedFlag())
						return conOper;
				}
			}
		}
		return null;
	}
}
