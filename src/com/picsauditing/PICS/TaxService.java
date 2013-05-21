package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.toggle.FeatureToggle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class TaxService {

	@Autowired
	protected InvoiceService invoiceService;
	@Autowired
	private FeatureToggle featureToggle;

	private static final Logger logger = LoggerFactory.getLogger(TaxService.class);

	public void applyTax(Invoice invoice) throws Exception {
		if (!isInvoiceTaxable(invoice)) {
			return;
		}

		InvoiceItem taxInvoiceItem = findTaxInvoiceItem(invoice);

		InvoiceFee taxInvoiceFee = null;
		if (taxInvoiceItem != null) {
			taxInvoiceFee = taxInvoiceItem.getInvoiceFee();
			initializeTaxInvoiceFee(taxInvoiceFee, invoice);
		} else {
			try {
				taxInvoiceFee = createTaxInvoiceFee(invoice);
			} catch (Exception e) {
				logger.error("Failed to create a taxInvoiceFee for invoice {}", invoice.getId(), e);
			}
		}

		applyTaxInvoiceFeeToInvoice(invoice, taxInvoiceFee);
		invoice.updateAmount();
	}

	private void initializeTaxInvoiceFee(InvoiceFee invoiceFee, Invoice invoice) throws Exception {
		if (!FeeClass.CanadianTax.equals(invoiceFee.getFeeClass())) {
			return;
		}

		CountrySubdivision countrySubdivision = invoice.getAccount().getCountrySubdivision();
		InvoiceFeeCountry provinceTaxFee = invoiceService.getProvinceTaxFee(countrySubdivision);
		invoiceFee.setSubdivisionFee(provinceTaxFee);
	}

	private boolean isInvoiceTaxable(Invoice invoice) {
		return invoice.getCurrency().isTaxable();
	}

	private InvoiceFee createTaxInvoiceFee(Invoice invoice) throws Exception {
		Currency currency = invoice.getCurrency();

		if (currency.isCAD()) {
			if (featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)) {
				return createNewCanadianTaxInvoiceFee(invoice);
			} else {
				return createLegacyCanadianTaxInvoiceFee(invoice);
			}
		} else if(currency.isGBP()){
			return createVatTaxInvoiceFee();
		} else {
			return null;
		}
	}

	private InvoiceFee createVatTaxInvoiceFee() {
		InvoiceFee vatInvoiceFee = new InvoiceFee(InvoiceFee.VAT);
		vatInvoiceFee.setFeeClass(FeeClass.VAT);
		return vatInvoiceFee;
	}

	private InvoiceFee createLegacyCanadianTaxInvoiceFee(Invoice invoice) {
		InvoiceFee canadianInvoiceFee = new InvoiceFee(InvoiceFee.GST);
		canadianInvoiceFee.setFeeClass(FeeClass.GST);
		return canadianInvoiceFee;
	}

	private InvoiceFee createNewCanadianTaxInvoiceFee(Invoice invoice) throws Exception {
		CountrySubdivision countrySubdivision = invoice.getAccount().getCountrySubdivision();

		try {
			return invoiceService.getCanadianTaxInvoiceFeeForProvince(countrySubdivision);
		} catch (Exception e) {
			throw new Exception("Unable to create an Canadian tax invoiceFee for invoice id: " + invoice.getId(), e);
		}
	}


	private void applyTaxInvoiceFeeToInvoice(Invoice invoice, InvoiceFee taxInvoiceFee) {
		if (!shouldApplyTaxInvoiceFee(taxInvoiceFee)) {
			return;
		}

		BigDecimal taxAmount = calculateTaxAmountForInvoice(invoice, taxInvoiceFee);

		InvoiceItem taxItem = findTaxInvoiceItemInInvoice(taxInvoiceFee, invoice);
		if (taxItem == null) {
			addNewTaxItemToInvoice(invoice, taxInvoiceFee, taxAmount);
		} else {
			if (taxItemNeedsToBeUpdated(taxItem, taxAmount)) {
				updateExistingTaxItemInInvoice(invoice, taxItem, taxAmount);
			}
		}
	}

	private boolean shouldApplyTaxInvoiceFee(InvoiceFee taxInvoiceFee) {
		return taxInvoiceFee != null;
	}

	private BigDecimal calculateTaxAmountForInvoice(Invoice invoice, InvoiceFee taxInvoiceFee) {
		BigDecimal invoiceTotalBeforeTax = calculateInvoiceTotalBeforeTax(invoice, taxInvoiceFee);
		return calculateTaxAmount(taxInvoiceFee, invoiceTotalBeforeTax);
	}

	private BigDecimal calculateInvoiceTotalBeforeTax(Invoice invoice, InvoiceFee taxInvoiceFee) {
		BigDecimal invoiceTotalBeforeTax = createZeroAmount();

		for (InvoiceItem item : invoice.getItems()) {
			if (!item.getInvoiceFee().equals(taxInvoiceFee)) {
				invoiceTotalBeforeTax = invoiceTotalBeforeTax.add(item.getAmount());
			}
		}
		return invoiceTotalBeforeTax;
	}

	private BigDecimal calculateTaxAmount(InvoiceFee invoiceFee, BigDecimal invoiceTotalBeforeTax) {
		return invoiceFee.getTax(invoiceTotalBeforeTax);
	}

	private InvoiceItem findTaxInvoiceItemInInvoice(InvoiceFee taxInvoiceFee, Invoice invoice) {
		for (InvoiceItem item : invoice.getItems()) {
			if (item.getInvoiceFee().equals(taxInvoiceFee)) {
				return item;
			}
		}
		return null;
	}

	private void addNewTaxItemToInvoice(Invoice invoice, InvoiceFee taxInvoiceFee, BigDecimal taxAmount) {
		InvoiceItem taxItem = createNewTaxInvoiceItem(invoice, taxInvoiceFee, taxAmount);
		invoice.getItems().add(taxItem);
		AccountingSystemSynchronization.setToSynchronize(invoice);
	}

	private InvoiceItem createNewTaxInvoiceItem(Invoice invoice, InvoiceFee taxInvoiceFee, BigDecimal taxAmount) {
		InvoiceItem taxItem = createTaxInvoiceItemForAmount(taxInvoiceFee, taxAmount);
		taxItem.setInvoice(invoice);
		taxItem.setAuditColumns(new User(User.SYSTEM));

		return taxItem;
	}

	private InvoiceItem createTaxInvoiceItemForAmount(InvoiceFee taxInvoiceFee, BigDecimal taxAmount) {
		return new InvoiceItem(taxInvoiceFee, taxAmount, null);
	}

	private boolean taxItemNeedsToBeUpdated(InvoiceItem taxItem, BigDecimal taxAmount) {
		return !taxItem.getAmount().equals(taxAmount);
	}

	private void updateExistingTaxItemInInvoice(Invoice invoice, InvoiceItem taxItem, BigDecimal taxAmount) {
		updateTaxItem(taxItem, taxAmount);
		AccountingSystemSynchronization.setToSynchronize(invoice);
	}

	private void updateTaxItem(InvoiceItem taxItem, BigDecimal taxAmount) {
		taxItem.setAmount(taxAmount);
	}

	private BigDecimal createZeroAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		amount.setScale(2, BigDecimal.ROUND_UP);

		return amount;
	}

	InvoiceItem findTaxInvoiceItem(Invoice invoice) {
		for (InvoiceItem item : invoice.getItems()) {
			InvoiceFee invoiceFee = item.getInvoiceFee();
			if (invoiceFee == null) {
				continue;
			}

			if (InvoiceService.TAX_FEE_CLASSES.contains(invoiceFee.getFeeClass())) {
				return item;
			}
		}

		return null;
	}
}
