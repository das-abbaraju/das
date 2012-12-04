package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.tools.generic.DateTool;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.PicsDateFormat;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("I")
public class Invoice extends Transaction {
	public final static int daysUntilDue = 30;

	private Date dueDate;
	private String poNumber;
	private String notes;
	private Date paidDate; // MAX(Payment.creationDate)
	private Map<FeeClass, BigDecimal> commissionEligibleFeeMap;
	private BigDecimal totalCommissionEligibleFees;

	private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
	private List<PaymentAppliedToInvoice> payments = new ArrayList<PaymentAppliedToInvoice>();

	@Transient
	public boolean isOverdue() {
		if (totalAmount.compareTo(BigDecimal.ZERO) <= 0)
			return false;

		if (getStatus().isPaid() || getStatus().isVoid())
			return false;

		if (dueDate == null)
			return false;

		return dueDate.before(new Date());
	}

	@Temporal(TemporalType.DATE)
	@ReportField(category = FieldCategory.Invoicing, type = FieldType.Date)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@ReportField(category = FieldCategory.Invoicing, type = FieldType.Date)
	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	@ReportField(category = FieldCategory.Invoicing, type = FieldType.String)
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@ReportField(category = FieldCategory.Invoicing, type = FieldType.String)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@OneToMany(mappedBy = "invoice", cascade = { CascadeType.ALL })
	public List<InvoiceItem> getItems() {
		Collections.sort(items, new Comparator<InvoiceItem>() {
			@Override
			public int compare(InvoiceItem o1, InvoiceItem o2) {
				return o1.getInvoiceFee().getDisplayOrder().compareTo(o2.getInvoiceFee().getDisplayOrder());
			}
		});
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}

	/**
	 * This is used by the QBWebConnector Adaptor to ensure that all Tax items
	 * come before any other Fee item in an invoice for proper tax handling.
	 * 
	 * @return
	 */
	@Transient
	public List<InvoiceItem> getItemsSortedByTaxFirst() {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		items.addAll(getItems());
		Collections.sort(items, new Comparator<InvoiceItem>() {
			@Override
			public int compare(InvoiceItem o1, InvoiceItem o2) {
				Integer int1 = o1.getInvoiceFee().getId();
				Integer int2 = o2.getInvoiceFee().getId();

				return int1.compareTo(int2);
			}
		});
		return items;
	}

	@OneToMany(mappedBy = "invoice", cascade = { CascadeType.REMOVE })
	public List<PaymentAppliedToInvoice> getPayments() {
		return payments;
	}

	public void setPayments(List<PaymentAppliedToInvoice> payments) {
		this.payments = payments;
	}

	@PreUpdate
	@PrePersist
	public void preSave() {
		if (getCurrency().isTaxable()) {
			InvoiceItem taxItem = null;
			InvoiceFee taxFee = getCurrency().getTaxFee();

			BigDecimal invoiceTotal = BigDecimal.ZERO;
			invoiceTotal.setScale(2, BigDecimal.ROUND_UP);
			for (InvoiceItem item : getItems()) {
				if (item.getInvoiceFee().equals(taxFee)) {
					taxItem = item;
				} else {
					invoiceTotal = invoiceTotal.add(item.getAmount());
				}
			}

			// if no tax item found, create tax item
			if (taxItem == null) {
				taxItem = new InvoiceItem(taxFee, taxFee.getTax(invoiceTotal), null);
				taxItem.setInvoice(this);
				taxItem.setAuditColumns(new User(User.SYSTEM));
				getItems().add(taxItem);
				qbSync = true;
				// else validate tax amount
			} else if (!taxItem.getAmount().equals(taxFee.getTax(invoiceTotal))) {
				taxItem.setAmount(taxFee.getTax(invoiceTotal));
				qbSync = true;
			}

			updateAmount();
		}
	}

	@Transient
	public void markPaid(User u) {
		setStatus(TransactionStatus.Paid);
		this.setPaidDate(new Date());
		this.setAuditColumns(u);
	}

	@Transient
	public void updateAmount() {
		totalAmount = BigDecimal.ZERO;
		for (InvoiceItem item : items)
			totalAmount = totalAmount.add(item.getAmount());
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
		for (PaymentApplied ip : payments) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
		super.updateAmountApplied();
	}

	@Transient
	public boolean isCcRebill() {
		if (getAccount() instanceof ContractorAccount) {
			ContractorAccount contractor = (ContractorAccount) getAccount();
			return !getStatus().isPaid() && contractor.getPaymentMethod().isCreditCard() && contractor.isCcValid();
		} else
			return false;
	}

	@Transient
	public String getDueDateF() {
		DateTool dateTool = new DateTool();
		return dateTool.format(PicsDateFormat.Iso, getDueDate());
	}

	public boolean containsATaxLineItem() {
		for (InvoiceItem item : getItems()) {
			if (item.getInvoiceFee().isGST() || item.getInvoiceFee().isVAT())
				return true;
		}
		return false;
	}

	@Transient
	public Map<FeeClass, BigDecimal> getCommissionEligibleFees(boolean forceRecalc) {
		if (!forceRecalc && this.commissionEligibleFeeMap != null) {
			return this.commissionEligibleFeeMap;
		}

		if (CollectionUtils.isEmpty(this.getItems())) {
			return Collections.emptyMap();
		}
		
		this.totalCommissionEligibleFees = new BigDecimal(0.00);

		this.commissionEligibleFeeMap = new HashMap<FeeClass, BigDecimal>();
		for (InvoiceItem invoiceItem : this.getItems()) {
			InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();
			if (invoiceFee != null && invoiceItem.getInvoiceFee().isCommissionEligible()) {
				FeeClass feeClass = invoiceFee.getFeeClass();
				this.commissionEligibleFeeMap.put(feeClass, invoiceFee.getAmount());
				this.totalCommissionEligibleFees.add(invoiceFee.getAmount());
			}
		}

		return this.commissionEligibleFeeMap;
	}

	@Transient
	public BigDecimal getTotalCommissionEligibleInvoice(boolean forceRecalc) {
		if (this.totalCommissionEligibleFees == null || forceRecalc) {
			this.getCommissionEligibleFees(true);
		}

		return this.totalCommissionEligibleFees;
	}
}
