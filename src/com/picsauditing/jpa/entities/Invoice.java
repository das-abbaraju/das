package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.persistence.Column;

import com.picsauditing.access.OpPerms;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.tools.generic.DateTool;

import com.picsauditing.PICS.TaxService;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.PicsDateFormat;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;

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
	protected boolean qbSyncWithTax;
	private Invoice lateFeeInvoice;

    private InvoiceType invoiceType;
    protected BigDecimal commissionableAmount = BigDecimal.ZERO;

	private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
	private List<PaymentAppliedToInvoice> payments = new ArrayList<PaymentAppliedToInvoice>();
    private List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();

	@Transient
	public boolean hasTax() {
        return (getTaxItem() != null);
	}

    @Transient
    public InvoiceItem getTaxItem() {
        for (InvoiceItem item : items) {
            InvoiceFee invoiceFee = item.getInvoiceFee();
            if (invoiceFee == null) {
                continue;
            }

            if (TaxService.TAX_FEE_CLASSES.contains(invoiceFee.getFeeClass())) {
                return item;
            }
        }

        return null;
    }

    @Transient
	public boolean isOverdue() {
		if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}

		if (getStatus().isPaid() || getStatus().isVoid()) {
			return false;
		}

		if (dueDate == null) {
			return false;
		}

		return dueDate.before(new Date());
	}

	@Temporal(TemporalType.DATE)
	@ReportField(type = FieldType.Date, importance = FieldImportance.Average)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@ReportField(type = FieldType.Date, importance = FieldImportance.Average)
	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	@ReportField(type = FieldType.String)
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@ReportField(type = FieldType.String)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isQbSyncWithTax() {
		return qbSyncWithTax;
	}

	public void setQbSyncWithTax(boolean qbSyncWithTax) {
		this.qbSyncWithTax = qbSyncWithTax;
	}

	public Invoice getLateFeeInvoice() {
		return lateFeeInvoice;
	}

	public void setLateFeeInvoice(Invoice lateFeeInvoice) {
		this.lateFeeInvoice = lateFeeInvoice;
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

    @OneToMany(mappedBy = "invoice", cascade = { CascadeType.REMOVE })
    public List<CreditMemoAppliedToInvoice> getCreditMemos() {
        return creditMemos;
    }

    public void setCreditMemos(List<CreditMemoAppliedToInvoice> creditMemos) {
        this.creditMemos = creditMemos;
    }

    @Transient
	public void markPaid(User u) {
		setStatus(TransactionStatus.Paid);
		this.setPaidDate(new Date());
		this.setAuditColumns(u);
	}

	@Transient
	public void updateTotalAmount() {
		totalAmount = BigDecimal.ZERO;
        commissionableAmount = BigDecimal.ZERO;

		for (InvoiceItem item : items) {
			totalAmount = totalAmount.add(item.getAmount());

            if (item.getInvoiceFee().isCommissionEligible()) {
                commissionableAmount = commissionableAmount.add(item.getAmount());
            }
		}
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
		for (PaymentApplied ip : payments) {
			amountApplied = amountApplied.add(ip.getAmount());
		}

        for (CreditMemoAppliedToInvoice memo : creditMemos)
            amountApplied = amountApplied.add(memo.getAmount());

		super.updateAmountApplied();
	}

	@Transient
	public Map<FeeClass, BigDecimal> getCommissionEligibleFees(boolean forceRecalc) {
		if (!forceRecalc && this.commissionEligibleFeeMap != null) {
			return this.commissionEligibleFeeMap;
		}

		if (CollectionUtils.isEmpty(this.getItems())) {
			return Collections.emptyMap();
		}

		this.commissionEligibleFeeMap = new HashMap<FeeClass, BigDecimal>();
		for (InvoiceItem invoiceItem : this.getItems()) {
			InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();
			if (invoiceFee != null && invoiceItem.getInvoiceFee().isCommissionEligible()) {
				FeeClass feeClass = invoiceFee.getFeeClass();
				this.commissionEligibleFeeMap.put(feeClass, invoiceItem.getAmount());
			}
		}

		return this.commissionEligibleFeeMap;
	}

    @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.InvoiceType") })
    @Enumerated(EnumType.STRING)
    @Column(name = "invoiceType", nullable = false)
    @ReportField(type = FieldType.InvoiceType, importance = FieldImportance.Required)
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    @ReportField(type = FieldType.Float, importance = FieldImportance.Required, requiredPermissions = OpPerms.SalesCommission)
    public BigDecimal getCommissionableAmount() {
        return commissionableAmount;
    }

    public void setCommissionableAmount(BigDecimal commissionableAmount) {
        this.commissionableAmount = commissionableAmount;
	}

    @Transient
    public BigDecimal getTaxlessSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItem item : items)
            if (!item.getInvoiceFee().isTax()) {
                subtotal = item.getAmount().add(subtotal);
            }
        return subtotal;
    }
}
