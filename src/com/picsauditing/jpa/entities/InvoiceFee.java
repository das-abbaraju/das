package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_fee")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "daily")
public class InvoiceFee extends BaseTable {
	public final static int ACTIVATION = 1;
	public final static int ACTIVATION99 = 104;
	public final static int REACTIVATION = 2;
	public final static int FREE = 3;
	public final static int PQFONLY = 4;
	public final static int FACILITIES1 = 5;
	public final static int FACILITIES2 = 6;
	public final static int FACILITIES5 = 7;
	public final static int FACILITIES9 = 8;
	public final static int FACILITIES13 = 9;
	public final static int FACILITIES20 = 10;
	public final static int FACILITIES50 = 11;
	public final static int BIDONLY = 100;
	public final static int LATEFEE = 55;
	public final static int GST = 200;
	public final static int EXPEDITE = 51;
	public final static int RESCHEDULING = 54;
	private String fee;
	private BigDecimal amount = BigDecimal.ZERO;
	private boolean visible = true;
	private String feeClass;
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

	@Column( name = "defaultAmount")
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
	 * @return
	 */
	public String getFeeClass() {
		return feeClass;
	}

	public void setFeeClass(String feeClass) {
		this.feeClass = feeClass;
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
		return this.id == FREE;
	}
	
	@Transient
	public boolean isBidonly() {
		return this.id == BIDONLY;
	}

	@Transient
	public BigDecimal getGSTSurchage(BigDecimal total) {
		return total.multiply(BigDecimal.valueOf(0.05)).setScale(2,BigDecimal.ROUND_UP);
	}
}
