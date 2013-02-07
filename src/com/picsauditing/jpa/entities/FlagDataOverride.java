package com.picsauditing.jpa.entities;

import java.util.Date;

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
@Table(name = "flag_data_override")
public class FlagDataOverride extends BaseTable {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private ContractorOperator contractorOperator;
	private FlagCriteria criteria;
	private FlagColor forceflag;
	private Date forceEnd;
	private String year;

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
	public FlagColor getForceflag() {
		return forceflag;
	}

	public void setForceflag(FlagColor forceflag) {
		this.forceflag = forceflag;
	}

	@ReportField(type = FieldType.Date, category = FieldCategory.CompanyStatistics, importance = FieldImportance.Required)
	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}
	
	@ReportField(type = FieldType.String, category = FieldCategory.CompanyStatistics, importance = FieldImportance.Low)
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Transient
	public boolean isInForce() {
		if (forceEnd == null)
			return false;
		return forceEnd.after(new Date());
	}

    @Override
    public Object clone() {
        FlagDataOverride flagDataOverride = new FlagDataOverride();
        flagDataOverride.setCriteria(this.getCriteria());
        flagDataOverride.setContractor(this.getContractor());
        flagDataOverride.setContractorOperator(this.getContractorOperator());
        flagDataOverride.setOperator(this.getOperator());
        flagDataOverride.copyPayloadFrom(this);
        flagDataOverride.setAuditColumns();
        return flagDataOverride;
    }
    /**
     * FlagDataOverride has an unusual data architecture in that the overrides need to "track" the criteria they are
     * overriding from year to year, which requires adjusting the data at year's end. However,
     * a Hibernate fluke involving the order in which Delete's are performed (always last) means it's not a simple
     * matter of moving a record by deleting it and re-inserting it (because we get a key violation before the delet
     * actually occurs). So, we are getting around this by reusing existing records and updating them by shifting
     * the contents (the payload). The payload is all non-key fields.
     * @param source
     */
    public void copyPayloadFrom(FlagDataOverride source) {
        assert (source != null);
        this.setForceEnd(source.getForceEnd());
        this.setForceflag(source.getForceflag());
        this.setYear(source.getYear());

        this.setCreatedBy(source.getCreatedBy());
        this.setCreationDate(source.getCreationDate());
        this.setUpdateDate(source.getUpdateDate());
        this.setUpdatedBy(source.getUpdatedBy());
    }
    @Override
    public boolean equals(Object that) {
        if (!(that instanceof FlagDataOverride))
            return false;
        FlagDataOverride flagDataOverride = (FlagDataOverride) that;

        // See if we are missing key information that prevents from even knowing what this object represents,
        // much less that the two objects represent the same thing.
        if (flagDataOverride.getCriteria() == null || this.getCriteria() == null || flagDataOverride.getContractor() == null ||
                this.getContractor() == null || flagDataOverride.getOperator() == null || this.getOperator() == null
                || flagDataOverride.getContractorOperator() == null || this.getContractorOperator() == null)
            return false;

        // Note: We are purposefully NOT caring if the ID is the same or not
        // Compare everything else except the audit fields...
        if (this.getCriteria().getId() != flagDataOverride.getCriteria().getId())
            return false;
        if (this.getContractor().getId() != flagDataOverride.getContractor().getId())
            return false;
        if (this.getOperator().getId() != flagDataOverride.getOperator().getId())
            return false;
        if (this.getContractorOperator().getId() != flagDataOverride.getContractorOperator().getId())
            return false;
        if (this.getForceflag() != flagDataOverride.getForceflag())
            return false;
        if (this.getForceEnd() != flagDataOverride.getForceEnd())
            return false;
        if (this.getYear() != flagDataOverride.getYear())
            return false;

        return true;
    }
}
