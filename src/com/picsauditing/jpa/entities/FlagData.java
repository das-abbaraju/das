package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_data")
public class FlagData extends BaseTable implements Comparable<FlagData> {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private ContractorOperator contractorOperator;
	private FlagCriteria criteria;
	private FlagColor flag;
	private FlagColor baselineFlag;
	private FlagCriteriaContractor criteriaContractor;

	@ManyToOne
	@JoinColumns(
		{ @JoinColumn(name = "opID", referencedColumnName = "genID", insertable=false, updatable=false),
		  @JoinColumn(name = "conID", referencedColumnName = "subID", insertable=false, updatable=false) })
	public ContractorOperator getContractorOperator() {
		return contractorOperator;
	}
	
	public void setContractorOperator(ContractorOperator contractorOperator) {
		this.contractorOperator = contractorOperator;
	}
	
	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne
	@JoinColumn(name = "criteriaID", nullable = false)
	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.FlagColor, category = FieldCategory.CompanyStatistics, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Required)
	public FlagColor getFlag() {
		return flag;
	}

	public void setFlag(FlagColor flag) {
		this.flag = flag;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.FlagColor, category = FieldCategory.CompanyStatistics, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Low)
	public FlagColor getBaselineFlag() {
		return baselineFlag;
	}

	public void setBaselineFlag(FlagColor baselineFlag) {
		this.baselineFlag = baselineFlag;
	}

	@Transient
	public FlagCriteriaContractor getCriteriaContractor() {
		return criteriaContractor;
	}

	public void setCriteriaContractor(FlagCriteriaContractor criteriaContractor) {
		this.criteriaContractor = criteriaContractor;
	}

	@Override
	public boolean equals(Object other) {
		FlagData fd = (FlagData) other;

		if (id > 0 && fd.getId() > 0)
			return super.equals(other);

		if (!contractor.equals(fd.getContractor()))
			return false;
		if (!operator.equals(fd.getOperator()))
			return false;
		if (!criteria.equals(fd.getCriteria()))
			return false;
		return true;
	}

	@Override
	public void update(BaseTable change) {
		FlagData fd = (FlagData) change;
		if (!equals(change))
			// Don't update flag data for the wrong contractor/operator/criteria
			return;

		if (!flag.equals(fd.getFlag())) {
			this.setFlag(fd.getFlag());
			this.setAuditColumns(new User(User.SYSTEM));
		}
	}

	@Override
	public int compareTo(FlagData o) {
		return criteria.compareTo(o.criteria);
	}
	
	public void resetBaseline() {
		this.setBaselineFlag(this.getFlag());
	}
}
