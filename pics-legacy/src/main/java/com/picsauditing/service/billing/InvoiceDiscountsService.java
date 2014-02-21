package com.picsauditing.service.billing;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public class InvoiceDiscountsService {

    @Autowired
    private InvoiceFeeDAO invoiceFeeDAO;
    @Autowired
    private TopLevelOperatorFinder topLevelOperatorFinder;

    public static final OperatorAccount SUNCOR = OperatorAccount.builder().id(OperatorAccount.SUNCOR).build();

    public List<InvoiceItem> applyDiscounts(ContractorAccount contractor, List<InvoiceItem> items) {
        return applySuncor2014Discounts(items, contractor);
    }

    private List<InvoiceItem> applySuncor2014Discounts(List<InvoiceItem> items, ContractorAccount contractor) {
        List<InvoiceItem> discountsToBeAdded = new ArrayList<>();
        List<OperatorAccount> topLevelOperators = topLevelOperatorFinder.findAllTopLevelOperators(contractor);

        if (topLevelOperators.size() <= 1 && topLevelOperators.contains(SUNCOR)) {
            for (InvoiceItem invoiceItem: items) {
                InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();

                InvoiceFee discountInvoiceFee = findSuncor2014DiscountFee(invoiceFee);
                if (discountInvoiceFee != null) {
                    discountsToBeAdded.add(createDiscount(discountInvoiceFee));
                }
            }
        }
        return discountsToBeAdded;
    }

    private InvoiceFee findSuncor2014DiscountFee(InvoiceFee invoiceFee) {
        return invoiceFeeDAO.findDiscountByNumberOfOperatorsAndClass(invoiceFee.getFeeClass(),
                invoiceFee.getMinFacilities(), OperatorAccount.SUNCOR);
    }

    private InvoiceItem createDiscount(InvoiceFee invoiceFee) {
        InvoiceItem discount = new InvoiceItem();

        discount.setInvoiceFee(invoiceFee);
        discount.setAmount(invoiceFee.getAmount().negate());
        discount.setOriginalAmount(new BigDecimal(0));
        return discount;
    }

    public List<InvoiceItem> applyProratedDiscounts(ContractorAccount contractor, List<ContractorFee> upgradedFees, double daysUntilExpiration) {
        return applySuncor2014ProratedDiscounts(contractor, upgradedFees, daysUntilExpiration);
    }

    private List<InvoiceItem> applySuncor2014ProratedDiscounts(ContractorAccount contractor, List<ContractorFee> upgradedFees, double daysUntilExpiration) {
        List<InvoiceItem> proratedDiscounts = new ArrayList<>();


        if (isSingleOperatorContractor(contractor) && contractor.getLogoForSingleOperatorContractor().getId() == OperatorAccount.SUNCOR) {
            for (ContractorFee contractorFee: upgradedFees) {
                InvoiceFee discountFeeForPreviousLevel = findSuncor2014DiscountFee(contractorFee.getCurrentLevel());
                InvoiceFee discountFeeForNewLevel = findSuncor2014DiscountFee(contractorFee.getNewLevel());

                if (discountFeeForNewLevel != null && discountFeeForPreviousLevel != null) {
                    BigDecimal proratedAmount = InvoiceFeeProRater.calculateProRatedInvoiceFees(discountFeeForPreviousLevel, discountFeeForNewLevel, daysUntilExpiration);

                    if (proratedAmount.intValue() > 0) {
                        InvoiceItem proratedDiscount = createDiscount(discountFeeForNewLevel);
                        proratedDiscount.setAmount(proratedAmount.negate());
                        proratedDiscounts.add(proratedDiscount);
                    }
                }
            }
        }

        return proratedDiscounts;
    }

    private boolean isSingleOperatorContractor(ContractorAccount contractor) {
        return contractor.getLogoForSingleOperatorContractor() != null;
    }
}
