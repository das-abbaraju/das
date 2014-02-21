package com.picsauditing.service.contractor;

import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceFeeCountry;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PricingTiersBuilder {

    public static final int OLD_PRICING_TIER_9 = 9;
    public static final int OLD_PRICING_TIER_13 = 13;
    public static final int OLD_PRICING_TIER_5 = 5;
    public static final int NEW_PRICING_TIER_19 = 19;

    @Autowired
    private InvoiceFeeCountryDAO invoiceFeeCountryDAO;


    public List<PricingTier> buildPricingTiersForCountry(Country country, List<FeeClass> applicableFeeClasses,
                                                         int numberOfFacilities) {
        Set<FeeClass> feeTypes = FeeClass.getContractorPriceTableFeeTypes();
        List<InvoiceFeeCountry> countryFees = findRawInvoiceFees(country, feeTypes);

        countryFees = filterOutAndRestructureRedundantInvoiceFees(countryFees);

        return buildPricingTiersFromInvoiceFees(countryFees, applicableFeeClasses, numberOfFacilities);
    }

    private List<InvoiceFeeCountry> findRawInvoiceFees(Country country, Set<FeeClass> feeTypes) {
        // Look for the specific country
        List<InvoiceFeeCountry> countryFees = invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(country, feeTypes);

        // If that wasn't found, look up US as default
        if (CollectionUtils.isEmpty(countryFees)) {
            countryFees = invoiceFeeCountryDAO.findVisibleByCountryAndFeeClassList(new Country("US"), feeTypes);
        }
        return countryFees;
    }

    private List<PricingTier> buildPricingTiersFromInvoiceFees(List<InvoiceFeeCountry> countryFees,
                                                               List<FeeClass> applicableFeeClasses, int numberOfFacilities) {
        List<PricingTier> pricingTiers = new ArrayList<>();
        for (InvoiceFeeCountry feeCountry : countryFees) {
            InvoiceFee invoiceFee = feeCountry.getInvoiceFee();
            if (invoiceFee.getFeeClass() == FeeClass.Activation) {
                continue;
            }

            String level = invoiceFee.getMinFacilities() + "-" + invoiceFee.getMaxFacilities();

            PricingTier pricingTier = null;
            for (PricingTier inList : pricingTiers) {
                if (inList.getLevel().equals(level)) {
                    pricingTier = inList;
                    break;
                }
            }

            if (pricingTier == null) {
                pricingTier = new PricingTier(level, new ArrayList<PricingAmount>());
                pricingTiers.add(pricingTier);
            }

            PricingAmount pricingAmount = new PricingAmount(invoiceFee.getFeeClass(), feeCountry.getAmount());
            pricingAmount.setApplies(isPricingAmountApplicable(applicableFeeClasses, numberOfFacilities, invoiceFee));
            pricingTier.getPricingAmounts().add(pricingAmount);
        }

        return pricingTiers;
    }

    private boolean isPricingAmountApplicable(List<FeeClass> applicableFeeClasses, int numberOfFacilities, InvoiceFee invoiceFee) {
        return applicableFeeClasses.contains(invoiceFee.getFeeClass())
                && (invoiceFee.getMinFacilities() <= numberOfFacilities)
                && (invoiceFee.getMaxFacilities() >= numberOfFacilities);
    }

    private List<InvoiceFeeCountry> filterOutAndRestructureRedundantInvoiceFees(List<InvoiceFeeCountry> countryFees) {
        /*
            2013 Pricing Tiers:
            1-1
            2-4
            5-8
            9- 12
            13- 19
            20- 49
            50- 1000

            2014 Pricing Tiers:
            1-1
            2-4
            5-19
            20-49
            50-1000

            Instead of restructuring the tiers for 2014, we kept the 2013 tier but charged the same amount for 5-8,
            9-12, and 13-19.  This effectively does what we want on the billing side, but we have to adjust the display
            so that all those tiers are collapsed into 5-19.  So, this is just a hack in order to get the display to
            look nice without repetitive tiers.
         */

        CollectionUtils.filter(countryFees, new GenericPredicate<InvoiceFeeCountry>() {
            @Override
            public boolean evaluateEntity(InvoiceFeeCountry invoiceFeeCountry) {
                InvoiceFee invoiceFee = invoiceFeeCountry.getInvoiceFee();
                return invoiceFee.getMinFacilities() != OLD_PRICING_TIER_9 && invoiceFee.getMinFacilities() != OLD_PRICING_TIER_13;
            }
        });

        return restructureInvoiceFees(countryFees);
    }

    private List<InvoiceFeeCountry> restructureInvoiceFees(List<InvoiceFeeCountry> countryFees) {
            for (InvoiceFeeCountry fee : countryFees) {
            InvoiceFee invoiceFee = fee.getInvoiceFee();

            if (invoiceFee.getMinFacilities() == OLD_PRICING_TIER_5) {
                invoiceFee.setMaxFacilities(NEW_PRICING_TIER_19);
            }
        }

        return countryFees;
    }
}
