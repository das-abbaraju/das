package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.PICS.TaxService;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.model.i18n.TranslatableString;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_fee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class InvoiceFee extends BaseTable {
	public final static int LATEFEE = 336;
	public final static int OLDLATEFEE = 55;
	public final static int GST = 200;
	public final static int VAT = 201;
	public final static int IMPORTFEE = 340;
	public final static int IMPORTFEEZEROLEVEL = 343;

	private String fee;
	private String description;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal ratePercent = BigDecimal.ZERO;
	private boolean visible = true;
	private FeeClass feeClass;
	private int minFacilities;
	private int maxFacilities;
	private String qbFullName;
	private Date effectiveDate;
	private Integer displayOrder = 999;
	private boolean commissionEligible;
	private List<InvoiceFeeCountry> invoiceFeeCountries = new ArrayList<InvoiceFeeCountry>();
	private InvoiceFeeCountry regionalFee;

	public InvoiceFee() {
	}

	public InvoiceFee(int id) {
		this.id = id;
	}

	@Transient
	public String getFee() {
		if (fee != null) {
			return fee;
		}

		return new TranslatableString(getI18nKey("fee")).toTranslatedString();
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	@Transient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "defaultAmount", nullable = false)
    @ReportField(type = FieldType.Float)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

    @ReportField(type = FieldType.Float)
	public BigDecimal getRatePercent() {
		return ratePercent;
	}

	public void setRatePercent(BigDecimal ratePercent) {
		this.ratePercent = ratePercent;
	}

    @Transient
    public BigDecimal getRateDecimal() {
        return ratePercent.divide(new BigDecimal(100));
    }

    public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Activation, Membership, Misc, Free, Other
	 * 
	 * @return
	 */
	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.FeeClass, importance = FieldImportance.Average)
	public FeeClass getFeeClass() {
		return feeClass;
	}

	public void setFeeClass(FeeClass feeClass) {
		this.feeClass = feeClass;
	}

	public int getMinFacilities() {
		return minFacilities;
	}

	public void setMinFacilities(int minFacilities) {
		this.minFacilities = minFacilities;
	}

	@ReportField(type = FieldType.Integer, importance = FieldImportance.Average)
	public int getMaxFacilities() {
		return maxFacilities;
	}

	public void setMaxFacilities(int maxFacilities) {
		this.maxFacilities = maxFacilities;
	}

	public String getQbFullName() {
		return qbFullName;
	}

	public void setQbFullName(String qbFullName) {
		this.qbFullName = qbFullName;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

    @ReportField(type = FieldType.Boolean)
	public boolean isCommissionEligible() {
		return commissionEligible;
	}

	public void setCommissionEligible(boolean commissionEligible) {
		this.commissionEligible = commissionEligible;
	}

	@OneToMany(mappedBy = "invoiceFee", cascade = CascadeType.REMOVE)
	public List<InvoiceFeeCountry> getInvoiceFeeCountries() {
		return invoiceFeeCountries;
	}

	public void setInvoiceFeeCountries(List<InvoiceFeeCountry> invoiceFeeCountries) {
		this.invoiceFeeCountries = invoiceFeeCountries;
	}

	@Transient
	public boolean isFree() {
		return this.getMaxFacilities() == 0 && this.getMinFacilities() == 0;
	}

	@Transient
	public boolean isBidonly() {
		return this.getFeeClass() == FeeClass.BidOnly;
	}

	@Transient
	public boolean isListonly() {
		return this.getFeeClass() == FeeClass.ListOnly;
	}

	@Transient
	public boolean isPqfonly() {
		return this.getFeeClass() == FeeClass.DocuGUARD;
	}

	@Transient
	public boolean isMembership() {
		return this.getFeeClass().isMembership();
	}

	@Transient
	public boolean isActivation() {
		return this.getFeeClass() == FeeClass.Activation;
	}

	@Transient
	public boolean isReactivation() {
		return this.getFeeClass() == FeeClass.Reactivation;
	}

	@Transient
	public boolean isLegacyGST() {
		return getId() == GST;
	}

    @Transient
    public boolean isCanadianTax() {
        return this.getFeeClass() == FeeClass.CanadianTax;
    }

    @Transient
	public boolean isVAT() {
		return getId() == VAT;
	}

	@Transient
	public boolean isImportFee() {
		return this.getFeeClass() == FeeClass.ImportFee;
	}

	@Transient
	public BigDecimal getTax(BigDecimal amountToTax) {
        BigDecimal subdivisionTaxRate = BigDecimal.ZERO;
        if (getRegionalFee() != null) {
            subdivisionTaxRate = getRegionalFee().getRateDecimal();
        }

        BigDecimal feeTaxRate = getRateDecimal();
        BigDecimal totalTaxRate = subdivisionTaxRate.add(feeTaxRate);

        return amountToTax.multiply(totalTaxRate).setScale(2, BigDecimal.ROUND_UP);
	}

	@Transient
	public boolean isLegacyMembership() {
		return (this.getId() >= 4 && this.getId() <= 11) || this.getId() == 105;
	}

	@Transient
	public InvoiceFeeCountry getRegionalFee() {
		return regionalFee;
	}

	public void setRegionalFee(InvoiceFeeCountry regionalFee) {
		this.regionalFee = regionalFee;
	}

	@Transient
	public boolean isTax() {
		if (TaxService.TAX_FEE_CLASSES.contains(feeClass)) {
			return true;
		}

		return false;
	}

}
