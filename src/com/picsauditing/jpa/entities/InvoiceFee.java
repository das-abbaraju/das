package com.picsauditing.jpa.entities;

import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name = "invoice_fee")
public class InvoiceFee extends BaseTable implements java.io.Serializable {

	private String fee;
	private int amount;
	private boolean visible;

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

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

}
