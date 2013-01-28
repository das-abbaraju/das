package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
	private TranslatableString fee;
	private TranslatableString description;
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
	private InvoiceFeeCountry subdivisionFee;

	public InvoiceFee() {
	}

	public InvoiceFee(int id) {
		this.id = id;
	}

	@Transient
	public TranslatableString getFee() {
		return fee;
	}

	public void setFee(TranslatableString fee) {
		this.fee = fee;
	}

	@Transient
	public TranslatableString getDescription() {
		return description;
	}

	public void setDescription(TranslatableString description) {
		this.description = description;
	}

	@Column(name = "defaultAmount", nullable = false)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRatePercent() {
		return ratePercent;
	}

	public void setRatePercent(BigDecimal ratePercent) {
		this.ratePercent = ratePercent;
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
		return this.getFeeClass() == FeeClass.ListOnly || this.getFeeClass() == FeeClass.BidOnly
				|| this.getFeeClass() == FeeClass.DocuGUARD || this.getFeeClass() == FeeClass.AuditGUARD
				|| this.getFeeClass() == FeeClass.InsureGUARD || this.getFeeClass() == FeeClass.EmployeeGUARD;
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
	public boolean isVAT() {
		return getId() == VAT;
	}

	@Transient
	public boolean isImportFee() {
		return this.getFeeClass() == FeeClass.ImportFee;
	}

	@Transient
	public BigDecimal getTax(BigDecimal amountToTax) {
		if (feeClass.equals(FeeClass.CanadianTax)) {
			BigDecimal provinceTaxRate = getSubdivisionFee().getRatePercent().divide(new BigDecimal(100));
			BigDecimal gstTaxRate = getRatePercent().divide(new BigDecimal(100));
			BigDecimal totalTaxRate = provinceTaxRate.add(gstTaxRate);
			return amountToTax.multiply(totalTaxRate).setScale(2, BigDecimal.ROUND_UP);
		} else if (isLegacyGST()) {
			return amountToTax.multiply(BigDecimal.valueOf(0.05)).setScale(2, BigDecimal.ROUND_UP);
		} else if (isVAT()) {
			return amountToTax.multiply(BigDecimal.valueOf(0.20)).setScale(2, BigDecimal.ROUND_UP);
		} else {
			return BigDecimal.ZERO;
		}
	}

	@Transient
	public boolean isLegacyMembership() {
		return (this.getId() >= 4 && this.getId() <= 11) || this.getId() == 105;
	}

	@Transient
	public InvoiceFeeCountry getSubdivisionFee() {
		return subdivisionFee;
	}

	public void setSubdivisionFee(InvoiceFeeCountry subdivisionFee) {
		this.subdivisionFee = subdivisionFee;
	}

}
