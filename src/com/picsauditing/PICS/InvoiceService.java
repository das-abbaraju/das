package com.picsauditing.PICS;

import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class InvoiceService {

	@Autowired
	protected InvoiceDAO invoiceDAO;

	@Autowired
	protected InvoiceFeeCountryDAO invoiceFeeCountryDAO;

	@Autowired
	protected TaxService taxService;

	public static final ArrayList<FeeClass> TAX_FEE_CLASSES = new ArrayList<FeeClass>() {{
		add(FeeClass.GST);
		add(FeeClass.CanadianTax);
		add(FeeClass.VAT);
	}};

	public Invoice saveInvoice(Invoice invoice) throws Exception {
		taxService.applyTax(invoice);
		validate(invoice);
		return invoiceDAO.save(invoice);
	}

	private void validate(Invoice invoice) throws InvoiceValidationException {
		if (invoiceContainDuplicateTaxItems(invoice)) {
			throw new InvoiceValidationException("Invoice contains duplicate tax items.");
		}
	}

	private boolean invoiceContainDuplicateTaxItems(Invoice invoice) {
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
}