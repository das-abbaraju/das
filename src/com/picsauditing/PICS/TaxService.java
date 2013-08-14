package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.util.*;

import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.model.billing.AccountingSystemSynchronization;

@SuppressWarnings("serial")
public class TaxService {

    @Autowired
    protected InvoiceFeeCountryDAO invoiceFeeCountryDAO;

    public static final ArrayList<FeeClass> TAX_FEE_CLASSES = new ArrayList<FeeClass>() {{
        add(FeeClass.GST);
        add(FeeClass.CanadianTax);
        add(FeeClass.VAT);
    }};

    public boolean validate(Invoice invoice) throws InvoiceValidationException {
        int duplicateCount = 0;

        for (InvoiceItem invoiceItem : invoice.getItems()) {
            if (TAX_FEE_CLASSES.contains(invoiceItem.getInvoiceFee().getFeeClass())) {
                duplicateCount += 1;
                if (duplicateCount > 1) {
                    return true;
                }
            }
        }
        return false;
    }

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

        InvoiceFee taxInvoiceFee = getTaxInvoiceFee(feeClass, country, countrySubdivision);

		applyTaxInvoiceFeeToInvoice(invoice, taxInvoiceFee);
		invoice.updateTotalAmount();
	}

    public InvoiceFee getTaxInvoiceFee(FeeClass feeClass, Country country, CountrySubdivision countrySubdivision) throws Exception {
        List<InvoiceFeeCountry> regionalInvoiceFees = getAllTaxFeesByRegion(feeClass, country, countrySubdivision);

        InvoiceFee taxInvoiceFee = null;
        if (regionalInvoiceFees != null && !regionalInvoiceFees.isEmpty()) {
            InvoiceFeeCountry regionalTaxFee = getEffectiveRegionalTaxFee(regionalInvoiceFees);

            if (regionalTaxFee != null) {
                taxInvoiceFee = regionalTaxFee.getInvoiceFee();
                taxInvoiceFee.setRegionalFee(regionalTaxFee);
            }
        }

        return taxInvoiceFee;
    }

    private List<InvoiceFeeCountry> getAllTaxFeesByRegion(FeeClass feeClass, Country country, CountrySubdivision countrySubdivision) throws RecordNotFoundException {
        List<InvoiceFeeCountry> invoiceFeeCountries = invoiceFeeCountryDAO.findAllInvoiceFeeCountrySubdivision(feeClass, countrySubdivision);

        if (invoiceFeeCountries == null || invoiceFeeCountries.isEmpty()) {
            invoiceFeeCountries = invoiceFeeCountryDAO.findAllInvoiceFeeCountry(feeClass, country);
        }

        return invoiceFeeCountries;
    }

    private InvoiceFeeCountry getEffectiveRegionalTaxFee(List<InvoiceFeeCountry> invoiceFeeCountries) throws Exception {
        if (invoiceFeeCountries.size() == 1) {
            return invoiceFeeCountries.get(0);
        }

        Collections.sort(invoiceFeeCountries, new Comparator<InvoiceFeeCountry>() {
            @Override
            public int compare(InvoiceFeeCountry i1, InvoiceFeeCountry i2) {
                return i1.getEffectiveDate().compareTo(i2.getEffectiveDate());
            }
        });
        Collections.reverse(invoiceFeeCountries);

        Date today = new DateTime().toDate();
        for (InvoiceFeeCountry invoiceFeeCountry : invoiceFeeCountries) {
            if (!today.before(invoiceFeeCountry.getEffectiveDate())) {
                return invoiceFeeCountry;
            }
        }

        return null;
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