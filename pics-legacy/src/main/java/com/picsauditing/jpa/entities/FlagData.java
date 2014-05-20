package com.picsauditing.jpa.entities;

import com.picsauditing.database.domain.UpdatableListItem;
import com.picsauditing.jpa.entities.builders.FlagDataBuilder;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_data")
public class FlagData extends BaseTable implements com.picsauditing.flagcalculator.FlagData, Comparable<FlagData>, UpdatableListItem {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private ContractorOperator contractorOperator;
	private FlagCriteria criteria;
	private FlagColor flag;
	private FlagColor baselineFlag;
	private FlagCriteriaContractor criteriaContractor;

	@ManyToOne
	@JoinColumns(
		{ @JoinColumn(name = "opID", referencedColumnName = "opID", insertable=false, updatable=false),
		  @JoinColumn(name = "conID", referencedColumnName = "conID", insertable=false, updatable=false) })
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
	@ReportField(type = FieldType.FlagColor, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Required)
	public FlagColor getFlag() {
		return flag;
	}

	public void setFlag(FlagColor flag) {
		this.flag = flag;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.FlagColor, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Low)
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
    public void update(UpdatableListItem change) {
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

    public static FlagDataBuilder builder() {
        return new FlagDataBuilder();
    }

    @Override
    @Transient
    public boolean isInsurance() {
        return criteria.isInsurance();
    }

    @Override
    @Transient
    public String getCriteriaCategory() {
        return criteria.getCategory().toString();
    }

    @Override
    @Transient
    public String getCriteriaLabel() {
        return criteria.getLabel();
    }

    @Override
    @Transient
    public String getFlagColor() {
        return flag.toString();
    }

    @Override
    @Transient
    public int getCriteriaID() {
        return criteria.getId();
    }

}
