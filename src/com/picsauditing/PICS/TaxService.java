package com.picsauditing.PICS;

import java.math.BigDecimal;

import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.model.billing.AccountingSystemSynchronization;

@SuppressWarnings("serial")
public class TaxService {

	@Autowired
	protected InvoiceService invoiceService;

	private static final Logger logger = LoggerFactory.getLogger(TaxService.class);

	public void applyTax(Invoice invoice) throws Exception {
        CountrySubdivision countrySubdivision = invoice.getAccount().getCountrySubdivision();
        Country country = invoice.getAccount().getCountry();

        if (!country.isTaxable())
			return;

        InvoiceItem taxItem = invoice.getTaxItem();

        FeeClass feeClass;
        if (taxItem != null) {
            feeClass = taxItem.getInvoiceFee().getFeeClass();
        }
        else {
            feeClass = country.getTaxFeeClass();
        }

        InvoiceFee taxInvoiceFee = null;

        taxInvoiceFee = invoiceService.getTaxInvoiceFee(feeClass, country, countrySubdivision);

		applyTaxInvoiceFeeToInvoice(invoice, taxInvoiceFee);
		invoice.updateTotalAmount();
	}

	private void applyTaxInvoiceFeeToInvoice(Invoice invoice, InvoiceFee taxInvoiceFee) {
		if (taxInvoiceFee == null)
			return;

        BigDecimal totalBeforeTax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
        InvoiceItem taxItem = null;

        for (InvoiceItem item : invoice.getItems()) {
            if (item.getInvoiceFee().equals(taxInvoiceFee))
                taxItem = item;
            else
                totalBeforeTax = totalBeforeTax.add(item.getAmount());
        }

        BigDecimal taxAmount = taxInvoiceFee.getTax(totalBeforeTax);

        updateInvoiceTax(invoice, taxInvoiceFee, taxItem, taxAmount);
	}

    private void updateInvoiceTax(Invoice invoice, InvoiceFee taxInvoiceFee, InvoiceItem taxItem, BigDecimal taxAmount) {
        if (taxItem == null) {
            taxItem = new InvoiceItem(taxInvoiceFee, taxAmount, null);
            taxItem.setInvoice(invoice);
            taxItem.setAuditColumns(new User(User.SYSTEM));
            invoice.getItems().add(taxItem);
        } else {
            if (taxItem.getAmount().equals(taxAmount))
                return;

            taxItem.setAmount(taxAmount);
        }

        AccountingSystemSynchronization.setToSynchronize(invoice);
    }
}