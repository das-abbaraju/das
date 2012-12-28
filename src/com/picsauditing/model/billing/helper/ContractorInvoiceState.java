package com.picsauditing.model.billing.helper;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.Invoice;

public class ContractorInvoiceState {

	private Invoice invoice;
	private Date paymentExpiresDate;
	private boolean activation;
	private boolean delinquent; // has a Late Fee
	private boolean reactivation;
	private boolean renewal;
	private boolean upgrade;

	public Invoice getInvoice() {
		return invoice;
	}

	protected void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Date getPaymentExpiresDate() {
		if (paymentExpiresDate == null) {
			paymentExpiresDate = calculatePaymentExpiresDate();
		}

		return paymentExpiresDate;
	}

	private Date calculatePaymentExpiresDate() {
		if (delinquent) {
			return DateBean.addDays(new Date(), -425);
		}

		return DateBean.addDays(new Date(), -360);
	}

	public boolean isActivation() {
		return activation;
	}

	protected void setActivation(boolean activation) {
		this.activation = activation;
	}

	public boolean isDelinquent() {
		return delinquent;
	}

	protected void setDelinquent(boolean delinquent) {
		this.delinquent = delinquent;
	}

	public boolean isReactivation() {
		return reactivation;
	}

	protected void setReactivation(boolean reactivation) {
		this.reactivation = reactivation;
	}

	public boolean isRenewal() {
		return renewal;
	}

	protected void setRenewal(boolean renewal) {
		this.renewal = renewal;
	}

	public boolean isUpgrade() {
		return upgrade;
	}

	protected void setUpgrade(boolean upgrade) {
		this.upgrade = upgrade;
	}

}
