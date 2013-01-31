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

	public InvoiceFee getCanadianTaxInvoiceFeeForProvince(CountrySubdivision countrySubdivision) throws Exception {

		InvoiceFeeCountry provinceTaxFee = getProvinceTaxFee(countrySubdivision);
		InvoiceFee canadianTaxInvoiceFee = provinceTaxFee.getInvoiceFee();

		canadianTaxInvoiceFee.setSubdivisionFee(provinceTaxFee);

		return canadianTaxInvoiceFee;
	}

	InvoiceFeeCountry getProvinceTaxFee(CountrySubdivision countrySubdivision) throws Exception {
		List<InvoiceFeeCountry> invoiceFeeCountries = getAllTaxFeesForProvince(countrySubdivision);
		return getProvinceTaxFeeCurrentlyInEffect(invoiceFeeCountries);
	}

	private List<InvoiceFeeCountry> getAllTaxFeesForProvince(CountrySubdivision countrySubdivision) throws RecordNotFoundException {
		List<InvoiceFeeCountry> invoiceFeeCountries = invoiceFeeCountryDAO.findAllInvoiceFeeCountry(FeeClass.CanadianTax, countrySubdivision);
		if (invoiceFeeCountries == null) {
			throw new RecordNotFoundException("InvoiceFeeCountry records for feeClass: "
					+ FeeClass.CanadianTax  + " and subdivision isoCode: " + countrySubdivision.getIsoCode());
		}
		return invoiceFeeCountries;
	}

	private InvoiceFeeCountry getProvinceTaxFeeCurrentlyInEffect(List<InvoiceFeeCountry> invoiceFeeCountries) throws Exception {
		if (invoiceFeeCountries.size() == 1) {
			return invoiceFeeCountries.get(0);
		}

		sortByEffectiveDateDecending(invoiceFeeCountries);
		Date today = getTodayDate();
		for (InvoiceFeeCountry invoiceFeeCountry : invoiceFeeCountries) {
			if (!today.before(invoiceFeeCountry.getEffectiveDate())) {
				return invoiceFeeCountry;
			}
		}
		throw new Exception("None of the invoiceFeeCountries in are currently in effect.");
	}

	private void sortByEffectiveDateDecending(List<InvoiceFeeCountry> invoiceFeeCountries) {
		Collections.sort(invoiceFeeCountries, new Comparator<InvoiceFeeCountry>() {
			@Override
			public int compare(InvoiceFeeCountry i1, InvoiceFeeCountry i2) {
				return i1.getEffectiveDate().compareTo(i2.getEffectiveDate());
			}
		});
		Collections.reverse(invoiceFeeCountries);
	}

	private Date getTodayDate() {
		return new DateTime().toDate();
	}
}
