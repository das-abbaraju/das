package com.picsauditing.jpa.entities;

import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name = "invoice_item")
public class InvoiceFee extends BaseTable implements java.io.Serializable {
	
	private int id;
	private String fee;
	private int amount;
	private boolean visible;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
