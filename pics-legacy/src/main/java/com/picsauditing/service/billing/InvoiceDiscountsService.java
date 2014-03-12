package com.picsauditing.service.billing;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

        if (worksForOnlySuncor(contractor)) {
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

    private boolean worksForOnlySuncor(ContractorAccount contractor) {
        List<OperatorAccount> topLevelOperators = topLevelOperatorFinder.findAllTopLevelOperators(contractor);
        return topLevelOperators.size() <= 1 && topLevelOperators.contains(SUNCOR);
    }

    private InvoiceFee findSuncor2014DiscountFee(InvoiceFee invoiceFee) {
        return findSuncor2014DiscountFee(invoiceFee.getFeeClass(), invoiceFee.getMinFacilities());
    }

    private InvoiceFee findSuncor2014DiscountFee(FeeClass feeClass, int minFacilities) {
        return invoiceFeeDAO.findDiscountByNumberOfOperatorsAndClass(feeClass,
                minFacilities, OperatorAccount.SUNCOR);
    }

    private InvoiceItem createDiscount(InvoiceFee invoiceFee, BigDecimal amount) {
        return InvoiceItem.builder()
                .invoiceFee(invoiceFee)
                .amount(amount)
                .originalAmount(new BigDecimal(0)).build();
    }

    private InvoiceItem createDiscount(InvoiceFee invoiceFee) {
        return createDiscount(invoiceFee, invoiceFee.getAmount().negate());
    }

    public List<InvoiceItem> applyProratedDiscounts(ContractorAccount contractor, List<ContractorFee> upgradedFees, double daysUntilExpiration) {
        return applySuncor2014ProratedDiscounts(contractor, upgradedFees, daysUntilExpiration);
    }

    private List<InvoiceItem> applySuncor2014ProratedDiscounts(ContractorAccount contractor, List<ContractorFee> upgradedFees, double daysUntilExpiration) {
        List<InvoiceItem> proratedDiscounts = new ArrayList<>();

        if (worksForOnlySuncor(contractor)) {
            for (ContractorFee contractorFee: upgradedFees) {
                InvoiceFee currentDiscountFee = findSuncor2014DiscountFee(contractorFee.getFeeClass(), contractorFee.getCurrentLevel().getMinFacilities());
                BigDecimal currentSuncorDiscount = currentDiscountFee == null ? new BigDecimal(0) : currentDiscountFee.getAmount().negate();

                InvoiceFee upgradeDiscountFee = findSuncor2014DiscountFee(contractorFee.getFeeClass(), contractorFee.getNewLevel().getMinFacilities());
                BigDecimal upgradeSuncorDiscount = upgradeDiscountFee == null ? new BigDecimal(0) : upgradeDiscountFee.getAmount().negate();

                BigDecimal proratedAmount = InvoiceFeeProRater.calculateProRatedInvoiceFees(currentSuncorDiscount, upgradeSuncorDiscount, daysUntilExpiration);

                proratedDiscounts.add(createDiscount(upgradeDiscountFee, proratedAmount));
            }
        }

        return proratedDiscounts;
    }
}
