package com.picsauditing.jpa.entities;

import com.picsauditing.jpa.entities.builders.ContractorAccountBuilder;
import com.picsauditing.jpa.entities.builders.ContractorFeeBuilder;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_fee")
public class ContractorFee extends BaseTable {
	private ContractorAccount contractor;
	private FeeClass feeClass;
	private InvoiceFee currentLevel;
	private BigDecimal currentAmount = BigDecimal.ZERO;
	private InvoiceFee newLevel;
	private BigDecimal newAmount = BigDecimal.ZERO;
    private int currentFacilityCount;
    private int newFacilityCount;

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@Enumerated(EnumType.STRING)
	@JoinColumn(name = "feeClass", nullable = false)
    @ReportField(type = FieldType.FeeClass, i18nKeyPrefix = "FeeClass", importance = FieldImportance.Average)
	public FeeClass getFeeClass() {
		return feeClass;
	}

	public void setFeeClass(FeeClass feeClass) {
		this.feeClass = feeClass;
	}

    @ReportField(type = FieldType.Integer, importance = FieldImportance.Average)
    public int getCurrentFacilityCount() {
        return currentFacilityCount;
    }

    @ReportField(type = FieldType.Integer, importance = FieldImportance.Average)
    public int getNewFacilityCount() {
        return newFacilityCount;
    }

    public void setCurrentFacilityCount(int currentFacilityCount) {
        this.currentFacilityCount = currentFacilityCount;
    }

    public void setNewFacilityCount(int newFacilityCount) {
        this.newFacilityCount = newFacilityCount;
    }

	@ManyToOne
	@JoinColumn(name = "currentLevel", nullable = false)
	public InvoiceFee getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(InvoiceFee currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
	}

    @ReportField(type = FieldType.Float, importance = FieldImportance.Required)
	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}

	@ManyToOne
	@JoinColumn(name = "newLevel", nullable = false)
	public InvoiceFee getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(InvoiceFee newLevel) {
		this.newLevel = newLevel;
	}

	public void setNewAmount(BigDecimal newAmount) {
		this.newAmount = newAmount;
	}

    @ReportField(type = FieldType.Float, importance = FieldImportance.Required)
	public BigDecimal getNewAmount() {
		return newAmount;
	}

	@Transient
	public boolean isUpgrade() {
        boolean increasedPrice = newAmount.compareTo(currentAmount) > 0;
        boolean increasedPayingFacilities = newFacilityCount > currentFacilityCount;
        return increasedPrice && increasedPayingFacilities;
	}

	@Transient
	public boolean willBeUpgradedBy(InvoiceFee newFeeLevel) {
		if (!getFeeClass().equals(newFeeLevel.getFeeClass()))
			throw new RuntimeException("FeeClass mismatch for upgrade comparison. Expected " + getFeeClass()
					+ " but was passed " + newFeeLevel.getFeeClass());

		return currentLevel.getMaxFacilities() < newFeeLevel.getMinFacilities();
	}

    public static ContractorFeeBuilder builder() {
        return new ContractorFeeBuilder();
    }
}
