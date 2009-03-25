package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "invoice")
public class Invoice extends BaseTable implements java.io.Serializable {
	public final static int daysUntilDue = 30;

	private Account account;
	private Date dueDate;
	private boolean paid;
	private int totalAmount;
	private Date paidDate;
	private PaymentMethod paymentMethod;
	private String checkNumber;
	private String transactionID;
	private String poNumber;
	private String ccNumber;
	private String notes;
	private boolean qbSync;
	protected String qbListID;
	protected String qbPaymentListID;
	
	
	
	private List<InvoiceItem> items = new ArrayList<InvoiceItem>();

	@ManyToOne
	@JoinColumn(name = "accountID")
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
	public boolean isOverdue() {
		if (totalAmount <= 0)
			return false;
		
		if (paid)
			return false;
		
		if (dueDate == null)
			return false;
		
		return dueDate.before(new Date());
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	@Enumerated(EnumType.STRING)
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@OneToMany(mappedBy = "invoice", cascade = { CascadeType.ALL })
	public List<InvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}

	@Column(name = "ccNumber")
	public String getCcNumber() {
		return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

	/**
	 * True if QuickBooks Web Connector needs to pull this record into QuickBooks
	 * @return
	 */
	public boolean isQbSync() {
		return qbSync;
	}

	public void setQbSync(boolean qbSync) {
		this.qbSync = qbSync;
	}

	/**
	 * Unique Customer ID in QuickBooks, sample: 31A0000-1151296183
	 * @return
	 */
	public String getQbListID() {
		return qbListID;
	}

	public void setQbListID(String qbListID) {
		this.qbListID = qbListID;
	}

	public String getQbPaymentListID() {
		return qbPaymentListID;
	}

	public void setQbPaymentListID(String qbPaymentListID) {
		this.qbPaymentListID = qbPaymentListID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		// We use to compare class names, but with Hibernate, the names get really weird
		// Now we just ignore the names and just cast it to an Account object
		// System.out.println("this.getClass() "+getClass().getName());
		// System.out.println("obj.getClass()  "+obj.getClass().getName());
		// System.out.println("obj.getClass().getSuperclass()  "+obj.getClass().getSuperclass().getName());
		try {
			// Try to cast this to an account
			final Invoice other = (Invoice) obj;
			if (id == other.getId())
				return true;
			return false;
		} catch (Exception e) {
			// something went wrong so these must not be equal
			return false;
		}
	}
	

}
