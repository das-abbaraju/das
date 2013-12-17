package com.picsauditing.PICS;

import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SapAppPropertyUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class TaxService {

    @Autowired
    protected InvoiceFeeCountryDAO invoiceFeeCountryDAO;
    @Autowired
    protected FeatureToggle featureToggleChecker;

    protected SapAppPropertyUtil sapAppPropertyUtil;

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
                    return false;
                }
            }
        }
        return true;
    }

    public void applyTax(Transaction transaction) throws Exception {
        CountrySubdivision countrySubdivision = transaction.getAccount().getCountrySubdivision();
        Country country = transaction.getAccount().getCountry();

        if (!isTaxable(country))
			return;

        TransactionItem taxItem = transaction.getTaxItem();

        FeeClass feeClass;
        if (taxItem != null) {
            feeClass = taxItem.getInvoiceFee().getFeeClass();
        }
        else {
            feeClass = getTaxFeeClass(country);
        }

        InvoiceFee taxInvoiceFee = getTaxInvoiceFee(feeClass, country, countrySubdivision);

		applyTaxInvoiceFeeToTransaction(transaction, taxInvoiceFee);
        transaction.updateTotalAmount();
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

	private void applyTaxInvoiceFeeToTransaction(Transaction transaction, InvoiceFee taxInvoiceFee) {
		if (taxInvoiceFee == null)
			return;

        BigDecimal totalBeforeTax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
        TransactionItem taxItem = null;

        for (TransactionItem item : transaction.getItems()) {
            if (item.getInvoiceFee().equals(taxInvoiceFee))
                taxItem = item;
            else
                totalBeforeTax = totalBeforeTax.add(item.getAmount());
        }

        BigDecimal taxAmount = taxInvoiceFee.getTax(totalBeforeTax);

        updateInvoiceTax(transaction, taxInvoiceFee, taxItem, taxAmount);
	}

    private void updateInvoiceTax(Transaction transaction, InvoiceFee taxInvoiceFee, TransactionItem taxItem, BigDecimal taxAmount) {
        if (taxItem == null) {
            if (transaction instanceof Invoice) {
                taxItem = new InvoiceItem(taxInvoiceFee, taxAmount, taxAmount, null);
            }
            else if (transaction instanceof InvoiceCreditMemo) {
                taxItem = new ReturnItem();
                taxItem.setInvoiceFee(taxInvoiceFee);
                taxItem.setAmount(taxAmount);
            }

            taxItem.setTransaction(transaction);
            taxItem.setAuditColumns(new User(User.SYSTEM));
            transaction.getItems().add(taxItem);
        } else {
            if (taxItem.getAmount().equals(taxAmount))
                return;

            taxItem.setAmount(taxAmount);
        }

        AccountingSystemSynchronization.setToSynchronize(transaction);
    }

    public boolean isTaxable(Country country) {
        return (getTaxFeeClass(country) != null);
    }

    public FeeClass getTaxFeeClass(Country country) {
        if(country.isCanada()) {
            return FeeClass.CanadianTax;
        }
        else {
            int SouthAfricaBusinessUnit = 5;

            if (country.isUK() ||
                    (country.isZA() && getSapAppPropertyUtil().isSAPBusinessUnitEnabled(SouthAfricaBusinessUnit))) {
                return FeeClass.VAT;
            }
        }

        return null;
    }

    public SapAppPropertyUtil getSapAppPropertyUtil() {
        if (sapAppPropertyUtil == null) {
            sapAppPropertyUtil = SapAppPropertyUtil.factory();
        }

        return sapAppPropertyUtil;
    }

    public void setSapAppPropertyUtil(SapAppPropertyUtil sapAppPropertyUtil) {
        this.sapAppPropertyUtil = sapAppPropertyUtil;
    }
}