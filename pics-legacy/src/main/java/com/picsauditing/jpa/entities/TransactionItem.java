package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transactionType", discriminatorType = DiscriminatorType.STRING)
public abstract class TransactionItem extends BaseTable{

    protected InvoiceFee invoiceFee;
    protected BigDecimal amount = BigDecimal.ZERO;
    protected String description;

	@ManyToOne
	@JoinColumn(name = "feeID")
	public InvoiceFee getInvoiceFee() {
		return invoiceFee;
	}

	public void setInvoiceFee(InvoiceFee invoiceFee) {
		this.invoiceFee = invoiceFee;
	}

    @ReportField(type = FieldType.Float, importance = FieldImportance.Required)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

    @ReportField(importance = FieldImportance.Average)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public abstract void setTransaction(Transaction transaction);
}