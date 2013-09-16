package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_payment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "paymentType", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force=true)
public abstract class TransactionApplied extends BaseTable {

    private BigDecimal amount = BigDecimal.ZERO;

    @ReportField(type = FieldType.Float, importance = FieldImportance.Required)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
