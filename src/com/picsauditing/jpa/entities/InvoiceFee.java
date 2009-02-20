package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_fee")
public class InvoiceFee extends BaseTable implements java.io.Serializable {
	public final static int ACTIVATION = 1;
	public final static int REACTIVATION = 2;
	public final static int FREE = 3;
	public final static int PQFONLY = 4;
	public final static int FACILITIES1 = 5;
	public final static int FACILITIES2 = 6;
	public final static int FACILITIES5 = 7;
	public final static int FACILITIES9 = 8;
	public final static int FACILITIES13 = 9;
	public final static int FACILITIES20 = 10;
	private String fee;
	private int amount;
	private boolean visible = true;
	private String feeClass;
	
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
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getFeeClass() {
		return feeClass;
	}

	public void setFeeClass(String feeClass) {
		this.feeClass = feeClass;
	}

}
