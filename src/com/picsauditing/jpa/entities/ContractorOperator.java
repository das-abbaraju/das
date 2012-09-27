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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BasicDynaBean;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.CollectionUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "generalcontractors")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorOperator extends BaseTable implements java.io.Serializable {
	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private ContractorOperatorRelationshipType type;
	private ApprovalStatus workStatus = ApprovalStatus.P;
	private FlagColor flagColor;
	private FlagColor baselineFlag;
	private Integer baselineApprover;
	private Date baselineApproved;
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
	private String flagDetail;
	private String baselineFlagDetail;
	private Date lastStepToGreenDate;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	public ContractorOperatorRelationshipType getType() {
		return type;
	}

	public void setType(ContractorOperatorRelationshipType type) {
		this.type = type;
	}

	@Transient
	public boolean isGeneralContractorType() {
		return ContractorOperatorRelationshipType.GeneralContractor.equals(getType());
	}

	@Transient
	public boolean isContractorOperatorType() {
		return ContractorOperatorRelationshipType.ContractorOperator.equals(getType());
	}

	/**
	 * Default to P and then be approved or rejected
	 * 
	 * @return P=Pending, Y=Yes, N=No
	 */
	@Enumerated(EnumType.STRING)
	@ReportField(filterType = FilterType.Enum, category = FieldCategory.CompanyStatus, importance = FieldImportance.Average)
	public ApprovalStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(ApprovalStatus workStatus) {
		this.workStatus = workStatus;
	}

	@Transient
	public boolean isWorkStatusApproved() {
		if (!getOperatorAccount().isCorporate())
			return workStatus.isYes();
		else
			return isChildrenWorkStatusEqual(ApprovalStatus.Y);
	}

	@Transient
	public boolean isWorkStatusRejected() {
		if (!getOperatorAccount().isCorporate())
			return workStatus.isNo();
		else
			return isChildrenWorkStatusEqual(ApprovalStatus.N);
	}

	@Transient
	public boolean isWorkStatusPending() {
		if (!getOperatorAccount().isCorporate())
			return workStatus.isPending();
		else
			return isChildrenWorkStatusEqual(ApprovalStatus.P);
	}

	@Transient
	public boolean isWorkStatusContractor() {
		if (!getOperatorAccount().isCorporate())
			return workStatus.isContractor();
		else
			return isChildrenWorkStatusEqual(ApprovalStatus.C);
	}

	private boolean isChildrenWorkStatusEqual(ApprovalStatus parentStatus) {
		Set<Integer> idList = getFacilityOperatorIds();
		if (CollectionUtils.isEmpty(idList)) {
			return false;
		}

		String ids = Strings.implode(idList, ",");
		String where = "subid = " + getContractorAccount().getId() + " AND workStatus = '" + parentStatus + "'"
				+ " AND genid IN (" + ids + ")";

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

	@Transient
	private Set<Integer> getFacilityOperatorIds() {
		Set<Integer> idList = new HashSet<Integer>();
		for (Facility facility : getOperatorAccount().getOperatorFacilities()) {
			if (facility.getOperator().getStatus().isActiveDemo()
					&& !facility.getOperator().isAutoApproveRelationships()) {
				idList.add(facility.getOperator().getId());
			}
		}

		return idList;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(filterType = FilterType.Enum, category = FieldCategory.CompanyStatus)
	public FlagColor getBaselineFlag() {
		return baselineFlag;
	}

	public void setBaselineFlag(FlagColor baselineFlag) {
		this.baselineFlag = baselineFlag;
	}

	public Integer getBaselineApprover() {
		return baselineApprover;
	}

	public void setBaselineApprover(Integer baselineApprover) {
		this.baselineApprover = baselineApprover;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getBaselineApproved() {
		return baselineApproved;
	}

	public void setBaselineApproved(Date baselineApproved) {
		this.baselineApproved = baselineApproved;
	}

	public void resetBaseline(Permissions permissions) {
		baselineFlag = flagColor;
		baselineFlagDetail = flagDetail;
		baselineApproved = new Date();
		if (permissions != null && permissions.getUserId() > 0)
			baselineApprover = permissions.getUserId();
		else
			baselineApprover = User.SYSTEM;

		for (FlagData fd : this.getFlagDatas())
			fd.resetBaseline();
	}

	@Enumerated(EnumType.STRING)
	@ReportField(filterType = FilterType.Enum, category = FieldCategory.CompanyStatus)
	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}

	@Temporal(TemporalType.DATE)
	@ReportField(filterType = FilterType.Date, category = FieldCategory.CompanyStatus)
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
	@ReportField(filterType = FilterType.Date, category = FieldCategory.CompanyStatus)
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
	@ReportField(filterType = FilterType.Enum, category = FieldCategory.CompanyStatus, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Required)
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
	@ReportField(filterType = FilterType.Enum, category = FieldCategory.CompanyStatus, i18nKeyPrefix = "WaitingOn", importance = FieldImportance.Average)
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

	public String getFlagDetail() {
		return flagDetail;
	}

	public void setFlagDetail(String flagDetail) {
		this.flagDetail = flagDetail;
	}

	public String getBaselineFlagDetail() {
		return baselineFlagDetail;
	}

	public void setBaselineFlagDetail(String baselineFlagDetail) {
		this.baselineFlagDetail = baselineFlagDetail;
	}

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "contractorOperator")
	public Set<FlagData> getFlagDatas() {
		return flagDatas;
	}

	public void setFlagDatas(Set<FlagData> flagDatas) {
		this.flagDatas = flagDatas;
	}

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, mappedBy = "contractorOperator")
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

	@ReportField(filterType = FilterType.Date, category = FieldCategory.CompanyStatus)
	public Date getLastStepToGreenDate() {
		return lastStepToGreenDate;
	}

	public void setLastStepToGreenDate(Date lastStepToGreenDate) {
		this.lastStepToGreenDate = lastStepToGreenDate;
	}
}
