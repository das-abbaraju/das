package com.picsauditing.jpa.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue(value = "R")
public class PaymentAppliedToRefund extends PaymentApplied {
	private Refund refund;

	@ManyToOne(optional = false)
	@JoinColumn(name = "refundID")
	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}
}
