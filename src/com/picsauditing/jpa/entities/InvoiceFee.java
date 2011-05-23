package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_fee")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "daily")
public class InvoiceFee extends BaseTable {
	public final static int LATEFEE = 336;
	private String fee;
	private BigDecimal amount = BigDecimal.ZERO;
	private boolean visible = true;
	private FeeClass feeClass;
	private int minFacilities;
	private int maxFacilities;
	private String qbFullName;
	private Integer displayOrder = 999;

	public InvoiceFee() {
	}

	public InvoiceFee(int id) {
		this.id = id;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	@Column(name = "defaultAmount", nullable = false)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	@Transient
	public boolean isFree() {
		return this.getMaxFacilities() == 0 && this.getMinFacilities() == 0;
	}

	@Transient
	public boolean isBidonly() {
		return this.getFeeClass() == FeeClass.ListOnly;
	}

	@Transient
	public boolean isPqfonly() {
		return this.getFeeClass() == FeeClass.DocuGUARD;
	}

	@Transient
	public boolean isMembership() {
		return this.getFeeClass() == FeeClass.ListOnly || this.getFeeClass() == FeeClass.DocuGUARD || this.getFeeClass() == FeeClass.AuditGUARD
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
	public boolean isGST() {
		return this.getFeeClass() == FeeClass.GST;
	}

	@Transient
	public BigDecimal getGSTSurchage(BigDecimal total) {
		return total.multiply(BigDecimal.valueOf(0.05)).setScale(2, BigDecimal.ROUND_UP);
	}
}
