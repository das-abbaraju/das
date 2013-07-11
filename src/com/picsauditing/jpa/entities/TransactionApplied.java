package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_payment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "paymentType", discriminatorType = DiscriminatorType.STRING)
public abstract class TransactionApplied extends BaseTable {

    private BigDecimal amount = BigDecimal.ZERO;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
