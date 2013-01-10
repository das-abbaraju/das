package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class TaxService {

	@Autowired
	protected InvoiceService invoiceService;

	public void applyTax(Invoice invoice) {
		if (isInvoiceTaxable(invoice)) {
			InvoiceFee taxInvoiceFee = null;
			try {
				taxInvoiceFee = createTaxInvoiceFee(invoice);
			} catch (Exception e) {
				e.printStackTrace();
			}
			applyTaxInvoiceFeeToInvoice(invoice, taxInvoiceFee);
			invoice.updateAmount();
		}
	}

	private boolean isInvoiceTaxable(Invoice invoice) {
		return invoice.getCurrency().isTaxable();
	}

	private InvoiceFee createTaxInvoiceFee(Invoice invoice) throws Exception {
		Currency currency = invoice.getCurrency();

		if (currency.isCAD()) {
			return createCanadianTaxInvoiceFee(invoice);
		} else if(currency.isGBP()){
			InvoiceFee vat = new InvoiceFee(InvoiceFee.VAT);
			vat.setFeeClass(FeeClass.VAT);
			return vat;
		} else {
			return null;
		}
	}

	private InvoiceFee createCanadianTaxInvoiceFee(Invoice invoice) throws Exception {
		CountrySubdivision countrySubdivision = invoice.getAccount().getCountrySubdivision();

		try {
			return invoiceService.getCanadianTaxInvoiceFeeForProvince(countrySubdivision);
		} catch (Exception e) {
			throw new Exception("Unable to create an Canadian tax invoiceFee for invoice id: " + invoice.getId(), e);
		}
	}


	private void applyTaxInvoiceFeeToInvoice(Invoice invoice, InvoiceFee taxInvoiceFee) {
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
		invoice.setQbSync(true);
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
		invoice.setQbSync(true);
	}

	private void updateTaxItem(InvoiceItem taxItem, BigDecimal taxAmount) {
		taxItem.setAmount(taxAmount);
	}

	private BigDecimal createZeroAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		amount.setScale(2, BigDecimal.ROUND_UP);

		return amount;
	}
}
